package com.banking.ledger.service;

import com.banking.ledger.domain.model.Account;
import com.banking.ledger.domain.model.LedgerEntry;
import com.banking.ledger.domain.model.Transaction;
import com.banking.ledger.domain.repository.AccountRepository;
import com.banking.ledger.domain.repository.LedgerEntryRepository;
import com.banking.ledger.domain.repository.TransactionRepository;
import com.banking.ledger.grpc.*;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("LedgerServiceImpl Unit Tests")
class LedgerServiceImplTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private LedgerEntryRepository ledgerEntryRepository;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @Mock
    private Counter transactionCounter;
    @Mock
    private Counter transactionSuccessCounter;
    @Mock
    private Counter transactionFailureCounter;
    @Mock
    private Timer transactionTimer;
    @Mock
    private Counter accountCreationCounter;
    @Mock
    private StreamObserver<TransactionResponse> transactionObserver;
    @Mock
    private StreamObserver<AccountResponse> accountObserver;
    @Mock
    private StreamObserver<BalanceResponse> balanceObserver;

    private LedgerServiceImpl ledgerService;
    private UUID fromAccountId;
    private UUID toAccountId;
    private Account fromAccount;
    private Account toAccount;

    @BeforeEach
    void setUp() {
        ledgerService = new LedgerServiceImpl(
                accountRepository,
                transactionRepository,
                ledgerEntryRepository,
                kafkaTemplate,
                transactionCounter,
                transactionSuccessCounter,
                transactionFailureCounter,
                transactionTimer,
                accountCreationCounter);

        fromAccountId = UUID.randomUUID();
        toAccountId = UUID.randomUUID();

        fromAccount = Account.builder()
                .id(fromAccountId)
                .userId(UUID.randomUUID())
                .currency("USD")
                .balance(new BigDecimal("1000.0000"))
                .version(0L)
                .build();

        toAccount = Account.builder()
                .id(toAccountId)
                .userId(UUID.randomUUID())
                .currency("USD")
                .balance(new BigDecimal("500.0000"))
                .version(0L)
                .build();
    }

    @Nested
    @DisplayName("CreateAccount Tests")
    class CreateAccountTests {

        @Test
        @DisplayName("Should create account successfully with valid input")
        void createAccount_Success() {
            CreateAccountRequest request = CreateAccountRequest.newBuilder()
                    .setUserId(UUID.randomUUID().toString())
                    .setCurrency("USD")
                    .build();

            when(accountRepository.save(any(Account.class))).thenAnswer(i -> {
                Account a = i.getArgument(0);
                a.setId(UUID.randomUUID());
                return a;
            });

            ledgerService.createAccount(request, accountObserver);

            verify(accountObserver).onNext(any(AccountResponse.class));
            verify(accountObserver).onCompleted();
            verify(accountObserver, never()).onError(any());
        }

        @Test
        @DisplayName("Should reject invalid UUID format")
        void createAccount_InvalidUUID() {
            CreateAccountRequest request = CreateAccountRequest.newBuilder()
                    .setUserId("not-a-uuid")
                    .setCurrency("USD")
                    .build();

            ledgerService.createAccount(request, accountObserver);

            ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
            verify(accountObserver).onError(errorCaptor.capture());
            assertTrue(errorCaptor.getValue() instanceof StatusRuntimeException);
            assertEquals(Status.Code.INVALID_ARGUMENT,
                    Status.fromThrowable(errorCaptor.getValue()).getCode());
        }

        @Test
        @DisplayName("Should reject unsupported currency")
        void createAccount_InvalidCurrency() {
            CreateAccountRequest request = CreateAccountRequest.newBuilder()
                    .setUserId(UUID.randomUUID().toString())
                    .setCurrency("XYZ")
                    .build();

            ledgerService.createAccount(request, accountObserver);

            ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
            verify(accountObserver).onError(errorCaptor.capture());
            assertEquals(Status.Code.INVALID_ARGUMENT,
                    Status.fromThrowable(errorCaptor.getValue()).getCode());
        }

        @ParameterizedTest
        @ValueSource(strings = { "USD", "EUR", "GBP", "JPY" })
        @DisplayName("Should accept valid currencies")
        void createAccount_ValidCurrencies(String currency) {
            CreateAccountRequest request = CreateAccountRequest.newBuilder()
                    .setUserId(UUID.randomUUID().toString())
                    .setCurrency(currency)
                    .build();

            when(accountRepository.save(any(Account.class))).thenAnswer(i -> {
                Account a = i.getArgument(0);
                a.setId(UUID.randomUUID());
                return a;
            });

            ledgerService.createAccount(request, accountObserver);

            verify(accountObserver).onNext(any(AccountResponse.class));
            verify(accountObserver).onCompleted();
        }
    }

    @Nested
    @DisplayName("GetBalance Tests")
    class GetBalanceTests {

        @Test
        @DisplayName("Should return balance for existing account")
        void getBalance_Success() {
            GetBalanceRequest request = GetBalanceRequest.newBuilder()
                    .setAccountId(fromAccountId.toString())
                    .build();

            when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount));

            ledgerService.getBalance(request, balanceObserver);

            ArgumentCaptor<BalanceResponse> responseCaptor = ArgumentCaptor.forClass(BalanceResponse.class);
            verify(balanceObserver).onNext(responseCaptor.capture());
            verify(balanceObserver).onCompleted();

            assertEquals("1000.0000", responseCaptor.getValue().getBalance());
            assertEquals("USD", responseCaptor.getValue().getCurrency());
        }

        @Test
        @DisplayName("Should return NOT_FOUND for non-existent account")
        void getBalance_AccountNotFound() {
            GetBalanceRequest request = GetBalanceRequest.newBuilder()
                    .setAccountId(UUID.randomUUID().toString())
                    .build();

            when(accountRepository.findById(any())).thenReturn(Optional.empty());

            ledgerService.getBalance(request, balanceObserver);

            ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
            verify(balanceObserver).onError(errorCaptor.capture());
            assertEquals(Status.Code.NOT_FOUND,
                    Status.fromThrowable(errorCaptor.getValue()).getCode());
        }
    }

    @Nested
    @DisplayName("PostTransaction Tests")
    class PostTransactionTests {

        @Test
        @DisplayName("Should process transfer successfully")
        void postTransaction_TransferSuccess() {
            PostTransactionRequest request = PostTransactionRequest.newBuilder()
                    .setFromAccountId(fromAccountId.toString())
                    .setToAccountId(toAccountId.toString())
                    .setAmount("100.00")
                    .setCurrency("USD")
                    .setReferenceId("ref-12345")
                    .setType("TRANSFER")
                    .build();

            when(transactionRepository.findByReferenceId("ref-12345")).thenReturn(Optional.empty());
            when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount));
            when(accountRepository.findById(toAccountId)).thenReturn(Optional.of(toAccount));
            when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> {
                Transaction t = i.getArgument(0);
                if (t.getId() == null)
                    t.setId(UUID.randomUUID());
                return t;
            });

            ledgerService.postTransaction(request, transactionObserver);

            verify(transactionObserver).onNext(any(TransactionResponse.class));
            verify(transactionObserver).onCompleted();
            verify(transactionCounter).increment();

            // Verify balance changes
            assertEquals(new BigDecimal("900.0000"), fromAccount.getBalance());
            assertEquals(new BigDecimal("600.0000"), toAccount.getBalance());

            // Verify ledger entries created
            verify(ledgerEntryRepository, times(2)).save(any(LedgerEntry.class));
        }

        @Test
        @DisplayName("Should reject duplicate reference ID")
        void postTransaction_DuplicateReferenceId() {
            PostTransactionRequest request = PostTransactionRequest.newBuilder()
                    .setFromAccountId(fromAccountId.toString())
                    .setToAccountId(toAccountId.toString())
                    .setAmount("100.00")
                    .setCurrency("USD")
                    .setReferenceId("ref-duplicate")
                    .setType("TRANSFER")
                    .build();

            when(transactionRepository.findByReferenceId("ref-duplicate"))
                    .thenReturn(Optional.of(Transaction.builder().build()));

            ledgerService.postTransaction(request, transactionObserver);

            ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
            verify(transactionObserver).onError(errorCaptor.capture());
            assertEquals(Status.Code.ALREADY_EXISTS,
                    Status.fromThrowable(errorCaptor.getValue()).getCode());
        }

        @Test
        @DisplayName("Should reject insufficient funds")
        void postTransaction_InsufficientFunds() {
            PostTransactionRequest request = PostTransactionRequest.newBuilder()
                    .setFromAccountId(fromAccountId.toString())
                    .setToAccountId(toAccountId.toString())
                    .setAmount("2000.00") // More than balance
                    .setCurrency("USD")
                    .setReferenceId("ref-insufficient")
                    .setType("TRANSFER")
                    .build();

            when(transactionRepository.findByReferenceId("ref-insufficient")).thenReturn(Optional.empty());
            when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount));
            when(accountRepository.findById(toAccountId)).thenReturn(Optional.of(toAccount));
            when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> {
                Transaction t = i.getArgument(0);
                if (t.getId() == null)
                    t.setId(UUID.randomUUID());
                return t;
            });

            ledgerService.postTransaction(request, transactionObserver);

            ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
            verify(transactionObserver).onError(errorCaptor.capture());
            assertEquals(Status.Code.FAILED_PRECONDITION,
                    Status.fromThrowable(errorCaptor.getValue()).getCode());
            verify(transactionFailureCounter).increment();
        }

        @Test
        @DisplayName("Should reject currency mismatch")
        void postTransaction_CurrencyMismatch() {
            fromAccount.setCurrency("EUR");

            PostTransactionRequest request = PostTransactionRequest.newBuilder()
                    .setFromAccountId(fromAccountId.toString())
                    .setToAccountId(toAccountId.toString())
                    .setAmount("100.00")
                    .setCurrency("USD")
                    .setReferenceId("ref-mismatch")
                    .setType("TRANSFER")
                    .build();

            when(transactionRepository.findByReferenceId("ref-mismatch")).thenReturn(Optional.empty());
            when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount));

            ledgerService.postTransaction(request, transactionObserver);

            ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
            verify(transactionObserver).onError(errorCaptor.capture());
            assertEquals(Status.Code.FAILED_PRECONDITION,
                    Status.fromThrowable(errorCaptor.getValue()).getCode());
        }

        @Test
        @DisplayName("Should reject negative amount")
        void postTransaction_NegativeAmount() {
            PostTransactionRequest request = PostTransactionRequest.newBuilder()
                    .setFromAccountId(fromAccountId.toString())
                    .setAmount("-100.00")
                    .setCurrency("USD")
                    .setReferenceId("ref-negative")
                    .setType("WITHDRAWAL")
                    .build();

            ledgerService.postTransaction(request, transactionObserver);

            ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
            verify(transactionObserver).onError(errorCaptor.capture());
            assertEquals(Status.Code.INVALID_ARGUMENT,
                    Status.fromThrowable(errorCaptor.getValue()).getCode());
        }

        @Test
        @DisplayName("Should reject zero amount")
        void postTransaction_ZeroAmount() {
            PostTransactionRequest request = PostTransactionRequest.newBuilder()
                    .setFromAccountId(fromAccountId.toString())
                    .setAmount("0")
                    .setCurrency("USD")
                    .setReferenceId("ref-zero")
                    .setType("WITHDRAWAL")
                    .build();

            ledgerService.postTransaction(request, transactionObserver);

            ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
            verify(transactionObserver).onError(errorCaptor.capture());
            assertEquals(Status.Code.INVALID_ARGUMENT,
                    Status.fromThrowable(errorCaptor.getValue()).getCode());
        }

        @Test
        @DisplayName("Should reject invalid reference ID format")
        void postTransaction_InvalidReferenceId() {
            PostTransactionRequest request = PostTransactionRequest.newBuilder()
                    .setFromAccountId(fromAccountId.toString())
                    .setAmount("100.00")
                    .setCurrency("USD")
                    .setReferenceId("ref with spaces!")
                    .setType("WITHDRAWAL")
                    .build();

            ledgerService.postTransaction(request, transactionObserver);

            ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
            verify(transactionObserver).onError(errorCaptor.capture());
            assertEquals(Status.Code.INVALID_ARGUMENT,
                    Status.fromThrowable(errorCaptor.getValue()).getCode());
        }

        @Test
        @DisplayName("Should reject transfer to same account")
        void postTransaction_SameAccountTransfer() {
            PostTransactionRequest request = PostTransactionRequest.newBuilder()
                    .setFromAccountId(fromAccountId.toString())
                    .setToAccountId(fromAccountId.toString()) // Same account
                    .setAmount("100.00")
                    .setCurrency("USD")
                    .setReferenceId("ref-same")
                    .setType("TRANSFER")
                    .build();

            when(transactionRepository.findByReferenceId("ref-same")).thenReturn(Optional.empty());
            when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount));

            ledgerService.postTransaction(request, transactionObserver);

            ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
            verify(transactionObserver).onError(errorCaptor.capture());
            assertEquals(Status.Code.INVALID_ARGUMENT,
                    Status.fromThrowable(errorCaptor.getValue()).getCode());
        }

        @Test
        @DisplayName("Should process deposit successfully")
        void postTransaction_DepositSuccess() {
            PostTransactionRequest request = PostTransactionRequest.newBuilder()
                    .setToAccountId(toAccountId.toString())
                    .setAmount("500.00")
                    .setCurrency("USD")
                    .setReferenceId("ref-deposit")
                    .setType("DEPOSIT")
                    .build();

            when(transactionRepository.findByReferenceId("ref-deposit")).thenReturn(Optional.empty());
            when(accountRepository.findById(toAccountId)).thenReturn(Optional.of(toAccount));
            when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> {
                Transaction t = i.getArgument(0);
                if (t.getId() == null)
                    t.setId(UUID.randomUUID());
                return t;
            });

            ledgerService.postTransaction(request, transactionObserver);

            verify(transactionObserver).onNext(any(TransactionResponse.class));
            verify(transactionObserver).onCompleted();
            assertEquals(new BigDecimal("1000.0000"), toAccount.getBalance());
        }

        @Test
        @DisplayName("Should process withdrawal successfully")
        void postTransaction_WithdrawalSuccess() {
            PostTransactionRequest request = PostTransactionRequest.newBuilder()
                    .setFromAccountId(fromAccountId.toString())
                    .setAmount("200.00")
                    .setCurrency("USD")
                    .setReferenceId("ref-withdrawal")
                    .setType("WITHDRAWAL")
                    .build();

            when(transactionRepository.findByReferenceId("ref-withdrawal")).thenReturn(Optional.empty());
            when(accountRepository.findById(fromAccountId)).thenReturn(Optional.of(fromAccount));
            when(transactionRepository.save(any(Transaction.class))).thenAnswer(i -> {
                Transaction t = i.getArgument(0);
                if (t.getId() == null)
                    t.setId(UUID.randomUUID());
                return t;
            });

            ledgerService.postTransaction(request, transactionObserver);

            verify(transactionObserver).onNext(any(TransactionResponse.class));
            verify(transactionObserver).onCompleted();
            assertEquals(new BigDecimal("800.0000"), fromAccount.getBalance());
        }

        @Test
        @DisplayName("Should reject amount exceeding maximum")
        void postTransaction_AmountExceedsMaximum() {
            PostTransactionRequest request = PostTransactionRequest.newBuilder()
                    .setFromAccountId(fromAccountId.toString())
                    .setAmount("9999999999999.00") // Exceeds max
                    .setCurrency("USD")
                    .setReferenceId("ref-huge")
                    .setType("WITHDRAWAL")
                    .build();

            ledgerService.postTransaction(request, transactionObserver);

            ArgumentCaptor<Throwable> errorCaptor = ArgumentCaptor.forClass(Throwable.class);
            verify(transactionObserver).onError(errorCaptor.capture());
            assertEquals(Status.Code.INVALID_ARGUMENT,
                    Status.fromThrowable(errorCaptor.getValue()).getCode());
        }
    }
}
