package com.banking.ledger.validation;

import com.banking.ledger.exception.InvalidInputException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Input validation utilities for banking-grade security.
 * All methods throw InvalidInputException on validation failure with sanitized
 * messages.
 */
@UtilityClass
@Slf4j
public class InputValidator {

    // Supported currencies (ISO 4217)
    private static final Set<String> VALID_CURRENCIES = Set.of(
            "USD", "EUR", "GBP", "JPY", "CHF", "CAD", "AUD", "NZD", "SGD", "HKD");

    // Reference ID pattern: alphanumeric with dashes/underscores, max 64 chars
    private static final Pattern REFERENCE_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]{1,64}$");

    // Maximum amount for a single transaction (configurable via properties in
    // production)
    private static final BigDecimal MAX_TRANSACTION_AMOUNT = new BigDecimal("1000000000");

    // Maximum page size for history queries
    private static final int MAX_PAGE_SIZE = 100;

    /**
     * Validates and parses a UUID string.
     * 
     * @param input     the UUID string
     * @param fieldName the field name for error messages
     * @return the parsed UUID
     * @throws InvalidInputException if the UUID is invalid
     */
    public static UUID validateUUID(String input, String fieldName) {
        if (input == null || input.isBlank()) {
            throw new InvalidInputException(fieldName + " is required");
        }
        try {
            return UUID.fromString(input.trim());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid UUID format for {}: [REDACTED]", fieldName);
            throw new InvalidInputException(fieldName + " has invalid format");
        }
    }

    /**
     * Validates a currency code against ISO 4217.
     * 
     * @param currency the currency code
     * @throws InvalidInputException if the currency is not supported
     */
    public static void validateCurrency(String currency) {
        if (currency == null || !VALID_CURRENCIES.contains(currency.toUpperCase())) {
            throw new InvalidInputException("Unsupported currency code");
        }
    }

    /**
     * Validates and parses an amount string.
     * 
     * @param amountStr the amount string
     * @return the parsed BigDecimal
     * @throws InvalidInputException if the amount is invalid
     */
    public static BigDecimal validateAmount(String amountStr) {
        if (amountStr == null || amountStr.isBlank()) {
            throw new InvalidInputException("Amount is required");
        }
        try {
            BigDecimal amount = new BigDecimal(amountStr.trim());
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidInputException("Amount must be positive");
            }
            if (amount.compareTo(MAX_TRANSACTION_AMOUNT) > 0) {
                log.warn("Transaction amount exceeds maximum: [REDACTED]");
                throw new InvalidInputException("Amount exceeds maximum allowed");
            }
            if (amount.scale() > 4) {
                throw new InvalidInputException("Amount precision exceeds 4 decimal places");
            }
            return amount;
        } catch (NumberFormatException e) {
            log.warn("Invalid amount format: [REDACTED]");
            throw new InvalidInputException("Invalid amount format");
        }
    }

    /**
     * Validates a reference ID for idempotency.
     * 
     * @param referenceId the reference ID
     * @throws InvalidInputException if the reference ID is invalid
     */
    public static void validateReferenceId(String referenceId) {
        if (referenceId == null || referenceId.isBlank()) {
            throw new InvalidInputException("Reference ID is required");
        }
        if (!REFERENCE_ID_PATTERN.matcher(referenceId).matches()) {
            log.warn("Invalid reference ID format: [REDACTED]");
            throw new InvalidInputException("Reference ID contains invalid characters");
        }
    }

    /**
     * Validates pagination parameters.
     * 
     * @param page the page number (0-indexed)
     * @param size the page size
     * @return sanitized page size (capped at MAX_PAGE_SIZE)
     */
    public static int validatePagination(int page, int size) {
        if (page < 0) {
            throw new InvalidInputException("Page number must be non-negative");
        }
        if (size <= 0) {
            throw new InvalidInputException("Page size must be positive");
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }

    /**
     * Sanitizes metadata to prevent injection attacks.
     * 
     * @param metadata the raw metadata
     * @return sanitized metadata (null-safe, length-limited)
     */
    public static String sanitizeMetadata(String metadata) {
        if (metadata == null) {
            return null;
        }
        // Limit length to prevent DoS
        String sanitized = metadata.length() > 4096 ? metadata.substring(0, 4096) : metadata;
        // Remove control characters
        return sanitized.replaceAll("[\\x00-\\x1F\\x7F]", "");
    }
}
