package com.banking.ledger.service;

import com.banking.ledger.domain.event.AccountCreatedEvent;
import com.banking.ledger.domain.event.LedgerTransactionPostedEvent;
import com.banking.ledger.domain.event.TransactionFailedEvent;
import com.banking.ledger.domain.model.Account;
import com.banking.ledger.domain.model.LedgerEntry;
import com.banking.ledger.domain.model.Transaction;
import com.banking.ledger.domain.repository.AccountRepository;
import com.banking.ledger.domain.repository.LedgerEntryRepository;
import com.banking.ledger.domain.repository.TransactionRepository;
import com.banking.ledger.exception.AccountNotFoundException;
import com.banking.ledger.exception.CurrencyMismatchException;
import com.banking.ledger.exception.DuplicateTransactionException;
import com.banking.ledger.exception.InsufficientFundsException;
import com.banking.ledger.exception.InvalidInputException;
import com.banking.ledger.grpc.*;
import com.banking.ledger.validation.InputValidator;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * gRPC service implementation for ledger operations.
 * Implements banking-grade security with ACID compliance.
 */
@GrpcService
@RequiredArgsConstructor
@Slf4j
public class LedgerServiceImpl extends LedgerServiceGrpc.LedgerServiceImplBase {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final LedgerEntryRepository ledgerEntryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Counter transactionCounter;
    private final Counter transactionSuccessCounter;
    private final Counter transactionFailureCounter;
    private final Timer transactionTimer;
    private final Counter accountCreationCounter;

    @Override
    @Transactional
    public void createAccount(CreateAccountRequest request, StreamObserver<AccountResponse> responseObserver) {
        try {
            // Input validation
            UUID userId = InputValidator.validateUUID(request.getUserId(), "user_id");
            InputValidator.validateCurrency(request.getCurrency());

            Account account = Account.builder()
                    .userId(userId)
                    .currency(request.getCurrency().toUpperCase())
                    .balance(BigDecimal.ZERO)
                    .build();

            account = accountRepository.save(account);

            final Account savedAccount = account;
            log.info("Account created: accountId={}, userId={}",
                    maskUUID(savedAccount.getId()), maskUUID(savedAccount.getUserId()));

            AccountResponse response = AccountResponse.newBuilder()
                    .setAccountId(savedAccount.getId().toString())
                    .setUserId(savedAccount.getUserId().toString())
                    .setCurrency(savedAccount.getCurrency())
                    .setBalance(savedAccount.getBalance().toPlainString())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            // Publish event after transaction commits
            registerAfterCommit(() -> {
                AccountCreatedEvent event = AccountCreatedEvent.builder()
                        .accountId(savedAccount.getId())
                        .userId(savedAccount.getUserId())
                        .currency(savedAccount.getCurrency())
                        .initialBalance(savedAccount.getBalance())
                        .build();
                kafkaTemplate.send("banking.account.created", savedAccount.getId().toString(), event);
                accountCreationCounter.increment();
            });

        } catch (InvalidInputException e) {
            log.warn("Invalid input for createAccount: {}", e.getMessage());
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            log.error("Error creating account", e);
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }

    @Override
    public void getBalance(GetBalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        try {
            UUID accountId = InputValidator.validateUUID(request.getAccountId(), "account_id");

            Account account = accountRepository.findById(accountId)
                    .orElseThrow(() -> new AccountNotFoundException("Account not found"));

            BalanceResponse response = BalanceResponse.newBuilder()
                    .setAccountId(account.getId().toString())
                    .setCurrency(account.getCurrency())
                    .setBalance(account.getBalance().toPlainString())
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

        } catch (InvalidInputException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (AccountNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            log.error("Error getting balance", e);
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void postTransaction(PostTransactionRequest request, StreamObserver<TransactionResponse> responseObserver) {
        Timer.Sample sample = Timer.start();
        transactionCounter.increment();

        String referenceId = null;
        try {
            // Validate all inputs first
            InputValidator.validateReferenceId(request.getReferenceId());
            referenceId = request.getReferenceId();

            BigDecimal amount = InputValidator.validateAmount(request.getAmount());
            InputValidator.validateCurrency(request.getCurrency());
            String currency = request.getCurrency().toUpperCase();
            String sanitizedMetadata = InputValidator.sanitizeMetadata(request.getMetadata());

            Transaction.TransactionType type;
            try {
                type = Transaction.TransactionType.valueOf(request.getType().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidInputException("Invalid transaction type");
            }

            // Idempotency check
            if (transactionRepository.findByReferenceId(referenceId).isPresent()) {
                throw new DuplicateTransactionException("Transaction already processed");
            }

            UUID fromAccountId = null;
            UUID toAccountId = null;
            Account fromAccount = null;
            Account toAccount = null;

            // Load and validate accounts
            if (request.hasFromAccountId() && !request.getFromAccountId().isEmpty()) {
                fromAccountId = InputValidator.validateUUID(request.getFromAccountId(), "from_account_id");
                fromAccount = accountRepository.findById(fromAccountId)
                        .orElseThrow(() -> new AccountNotFoundException("Source account not found"));
                if (!fromAccount.getCurrency().equals(currency)) {
                    throw new CurrencyMismatchException("Source account currency mismatch");
                }
            }

            if (request.hasToAccountId() && !request.getToAccountId().isEmpty()) {
                toAccountId = InputValidator.validateUUID(request.getToAccountId(), "to_account_id");
                toAccount = accountRepository.findById(toAccountId)
                        .orElseThrow(() -> new AccountNotFoundException("Destination account not found"));
                if (!toAccount.getCurrency().equals(currency)) {
                    throw new CurrencyMismatchException("Destination account currency mismatch");
                }
            }

            // Validate transaction type constraints
            validateTransactionType(type, fromAccount, toAccount);

            // Create transaction record
            Transaction transaction = Transaction.builder()
                    .referenceId(referenceId)
                    .type(type)
                    .status(Transaction.TransactionStatus.PENDING)
                    .metadata(sanitizedMetadata)
                    .build();

            transaction = transactionRepository.save(transaction);

            // Process the transaction
            processTransactionLogic(transaction, fromAccount, toAccount, amount);
            transaction.setStatus(Transaction.TransactionStatus.POSTED);
            transactionRepository.save(transaction);

            log.info("Transaction posted: txId={}, refId={}, type={}, amount=[REDACTED]",
                    maskUUID(transaction.getId()), referenceId, type);

            TransactionResponse response = TransactionResponse.newBuilder()
                    .setTransactionId(transaction.getId().toString())
                    .setStatus(transaction.getStatus().name())
                    .setMessage("Transaction completed successfully")
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            // Publish success event after commit
            final Transaction finalTransaction = transaction;
            final Account finalFromAccount = fromAccount;
            final Account finalToAccount = toAccount;
            final BigDecimal finalAmount = amount;

            registerAfterCommit(() -> {
                LedgerTransactionPostedEvent event = LedgerTransactionPostedEvent.builder()
                        .transactionId(finalTransaction.getId())
                        .referenceId(finalTransaction.getReferenceId())
                        .type(finalTransaction.getType().name())
                        .status(finalTransaction.getStatus().name())
                        .fromAccountId(finalFromAccount != null ? finalFromAccount.getId() : null)
                        .toAccountId(finalToAccount != null ? finalToAccount.getId() : null)
                        .amount(finalAmount)
                        .currency(currency)
                        .metadata(finalTransaction.getMetadata())
                        .build();
                kafkaTemplate.send("banking.ledger.posted", finalTransaction.getId().toString(), event);
                transactionSuccessCounter.increment();
            });

            sample.stop(transactionTimer);

        } catch (InvalidInputException e) {
            transactionFailureCounter.increment();
            sample.stop(transactionTimer);
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (DuplicateTransactionException e) {
            sample.stop(transactionTimer);
            responseObserver.onError(Status.ALREADY_EXISTS.withDescription(e.getMessage()).asRuntimeException());
        } catch (AccountNotFoundException e) {
            transactionFailureCounter.increment();
            publishFailureEvent(referenceId, e.getMessage());
            sample.stop(transactionTimer);
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (CurrencyMismatchException e) {
            transactionFailureCounter.increment();
            publishFailureEvent(referenceId, e.getMessage());
            sample.stop(transactionTimer);
            responseObserver.onError(Status.FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException());
        } catch (InsufficientFundsException e) {
            transactionFailureCounter.increment();
            publishFailureEvent(referenceId, "Insufficient funds");
            sample.stop(transactionTimer);
            responseObserver
                    .onError(Status.FAILED_PRECONDITION.withDescription("Insufficient funds").asRuntimeException());
        } catch (Exception e) {
            transactionFailureCounter.increment();
            log.error("Transaction failed: refId={}", referenceId, e);
            publishFailureEvent(referenceId, "Internal error");
            sample.stop(transactionTimer);
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }

    private void validateTransactionType(Transaction.TransactionType type, Account fromAccount, Account toAccount) {
        switch (type) {
            case TRANSFER:
                if (fromAccount == null || toAccount == null) {
                    throw new InvalidInputException("Transfer requires both source and destination accounts");
                }
                if (fromAccount.getId().equals(toAccount.getId())) {
                    throw new InvalidInputException("Cannot transfer to same account");
                }
                break;
            case DEPOSIT:
                if (toAccount == null) {
                    throw new InvalidInputException("Deposit requires destination account");
                }
                break;
            case WITHDRAWAL:
                if (fromAccount == null) {
                    throw new InvalidInputException("Withdrawal requires source account");
                }
                break;
        }
    }

    private void processTransactionLogic(Transaction transaction, Account fromAccount, Account toAccount,
            BigDecimal amount) {
        if (fromAccount != null) {
            // Debit fromAccount with balance check
            if (fromAccount.getBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException("Insufficient funds");
            }
            fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
            accountRepository.save(fromAccount); // Triggers optimistic lock check

            createLedgerEntry(transaction, fromAccount, amount.negate(), LedgerEntry.EntryDirection.DEBIT);
        }

        if (toAccount != null) {
            // Credit toAccount
            toAccount.setBalance(toAccount.getBalance().add(amount));
            accountRepository.save(toAccount);

            createLedgerEntry(transaction, toAccount, amount, LedgerEntry.EntryDirection.CREDIT);
        }
    }

    private void createLedgerEntry(Transaction transaction, Account account, BigDecimal amount,
            LedgerEntry.EntryDirection direction) {
        LedgerEntry entry = LedgerEntry.builder()
                .transaction(transaction)
                .account(account)
                .amount(amount)
                .direction(direction)
                .balanceAfter(account.getBalance())
                .build();
        ledgerEntryRepository.save(entry);
    }

    @Override
    public void getTransactionHistory(GetHistoryRequest request,
            StreamObserver<TransactionHistoryResponse> responseObserver) {
        try {
            UUID accountId = InputValidator.validateUUID(request.getAccountId(), "account_id");
            int pageSize = InputValidator.validatePagination(request.getPage(),
                    request.getSize() > 0 ? request.getSize() : 20);

            Page<LedgerEntry> page = ledgerEntryRepository.findByAccountIdOrderByCreatedAtDesc(
                    accountId, PageRequest.of(request.getPage(), pageSize));

            TransactionHistoryResponse.Builder historyBuilder = TransactionHistoryResponse.newBuilder();

            for (LedgerEntry entry : page.getContent()) {
                historyBuilder.addTransactions(TransactionSummary.newBuilder()
                        .setTransactionId(entry.getTransaction().getId().toString())
                        .setType(entry.getTransaction().getType().name())
                        .setAmount(entry.getAmount().abs().toPlainString())
                        .setDirection(entry.getDirection().name())
                        .setStatus(entry.getTransaction().getStatus().name())
                        .setCreatedAt(entry.getCreatedAt().toString())
                        .setBalanceAfter(entry.getBalanceAfter().toPlainString())
                        .setReferenceId(entry.getTransaction().getReferenceId())
                        .build());
            }

            responseObserver.onNext(historyBuilder.build());
            responseObserver.onCompleted();

        } catch (InvalidInputException e) {
            responseObserver.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            log.error("Error getting transaction history", e);
            responseObserver.onError(Status.INTERNAL.withDescription("Internal server error").asRuntimeException());
        }
    }

    /**
     * Registers a callback to execute after the current transaction commits.
     * This ensures Kafka events are only published after successful DB commit.
     */
    private void registerAfterCommit(Runnable callback) {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        callback.run();
                    } catch (Exception e) {
                        log.error("Error in after-commit callback", e);
                    }
                }
            });
        } else {
            // No active transaction, execute immediately
            callback.run();
        }
    }

    private void publishFailureEvent(String referenceId, String reason) {
        if (referenceId != null) {
            try {
                kafkaTemplate.send("banking.transaction.failed", referenceId,
                        new TransactionFailedEvent(referenceId, reason));
            } catch (Exception e) {
                log.error("Failed to publish failure event for refId={}", referenceId, e);
            }
        }
    }

    /**
     * Masks a UUID for logging (shows first 8 chars only).
     */
    private String maskUUID(UUID uuid) {
        if (uuid == null)
            return "null";
        String str = uuid.toString();
        return str.substring(0, 8) + "-****-****-****-************";
    }
}
