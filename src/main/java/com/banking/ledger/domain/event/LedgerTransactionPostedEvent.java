package com.banking.ledger.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerTransactionPostedEvent {
    private UUID transactionId;
    private String referenceId;
    private String type;
    private String status;
    private UUID fromAccountId; // Optional
    private UUID toAccountId; // Optional
    private BigDecimal amount;
    private String currency;
    private String metadata;
}
