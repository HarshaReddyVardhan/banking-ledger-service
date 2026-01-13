package com.banking.ledger.validation;

import com.banking.ledger.exception.InvalidInputException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InputValidator Unit Tests")
class InputValidatorTest {

    @Nested
    @DisplayName("UUID Validation")
    class UUIDValidationTests {

        @Test
        @DisplayName("Should accept valid UUID")
        void validateUUID_Valid() {
            UUID expected = UUID.randomUUID();
            UUID result = InputValidator.validateUUID(expected.toString(), "test_field");
            assertEquals(expected, result);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "  ", "not-a-uuid", "12345", "abc-def-ghi" })
        @DisplayName("Should reject invalid UUID formats")
        void validateUUID_Invalid(String input) {
            assertThrows(InvalidInputException.class,
                    () -> InputValidator.validateUUID(input, "test_field"));
        }
    }

    @Nested
    @DisplayName("Currency Validation")
    class CurrencyValidationTests {

        @ParameterizedTest
        @ValueSource(strings = { "USD", "EUR", "GBP", "JPY", "CHF", "CAD", "AUD", "usd", "Usd" })
        @DisplayName("Should accept valid currencies")
        void validateCurrency_Valid(String currency) {
            assertDoesNotThrow(() -> InputValidator.validateCurrency(currency));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "XYZ", "ABC", "123", "USDD", "US" })
        @DisplayName("Should reject invalid currencies")
        void validateCurrency_Invalid(String currency) {
            assertThrows(InvalidInputException.class,
                    () -> InputValidator.validateCurrency(currency));
        }
    }

    @Nested
    @DisplayName("Amount Validation")
    class AmountValidationTests {

        @Test
        @DisplayName("Should accept valid amount")
        void validateAmount_Valid() {
            BigDecimal result = InputValidator.validateAmount("100.50");
            assertEquals(new BigDecimal("100.50"), result);
        }

        @Test
        @DisplayName("Should trim whitespace")
        void validateAmount_Trimmed() {
            BigDecimal result = InputValidator.validateAmount("  50.00  ");
            assertEquals(new BigDecimal("50.00"), result);
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "  ", "abc", "12.34.56" })
        @DisplayName("Should reject invalid amount formats")
        void validateAmount_InvalidFormat(String amount) {
            assertThrows(InvalidInputException.class,
                    () -> InputValidator.validateAmount(amount));
        }

        @ParameterizedTest
        @ValueSource(strings = { "0", "-1", "-100.50" })
        @DisplayName("Should reject non-positive amounts")
        void validateAmount_NonPositive(String amount) {
            assertThrows(InvalidInputException.class,
                    () -> InputValidator.validateAmount(amount));
        }

        @Test
        @DisplayName("Should reject amount exceeding maximum")
        void validateAmount_ExceedsMax() {
            assertThrows(InvalidInputException.class,
                    () -> InputValidator.validateAmount("9999999999999.00"));
        }

        @Test
        @DisplayName("Should reject precision exceeding 4 decimal places")
        void validateAmount_TooMuchPrecision() {
            assertThrows(InvalidInputException.class,
                    () -> InputValidator.validateAmount("100.123456"));
        }

        @Test
        @DisplayName("Should accept amounts with 4 decimal places")
        void validateAmount_FourDecimalPlaces() {
            BigDecimal result = InputValidator.validateAmount("100.1234");
            assertEquals(new BigDecimal("100.1234"), result);
        }
    }

    @Nested
    @DisplayName("Reference ID Validation")
    class ReferenceIdValidationTests {

        @ParameterizedTest
        @ValueSource(strings = { "ref-123", "REF_456", "abc123", "a-b_c-d_e" })
        @DisplayName("Should accept valid reference IDs")
        void validateReferenceId_Valid(String refId) {
            assertDoesNotThrow(() -> InputValidator.validateReferenceId(refId));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = { "  ", "ref with space", "ref@123", "ref#456", "ref!test" })
        @DisplayName("Should reject invalid reference ID formats")
        void validateReferenceId_Invalid(String refId) {
            assertThrows(InvalidInputException.class,
                    () -> InputValidator.validateReferenceId(refId));
        }

        @Test
        @DisplayName("Should reject reference ID exceeding 64 characters")
        void validateReferenceId_TooLong() {
            String longRefId = "a".repeat(65);
            assertThrows(InvalidInputException.class,
                    () -> InputValidator.validateReferenceId(longRefId));
        }

        @Test
        @DisplayName("Should accept reference ID with exactly 64 characters")
        void validateReferenceId_MaxLength() {
            String maxRefId = "a".repeat(64);
            assertDoesNotThrow(() -> InputValidator.validateReferenceId(maxRefId));
        }
    }

    @Nested
    @DisplayName("Pagination Validation")
    class PaginationValidationTests {

        @Test
        @DisplayName("Should return valid page size")
        void validatePagination_Valid() {
            int result = InputValidator.validatePagination(0, 20);
            assertEquals(20, result);
        }

        @Test
        @DisplayName("Should cap page size at maximum")
        void validatePagination_CappedAtMax() {
            int result = InputValidator.validatePagination(0, 500);
            assertEquals(100, result);
        }

        @Test
        @DisplayName("Should reject negative page number")
        void validatePagination_NegativePage() {
            assertThrows(InvalidInputException.class,
                    () -> InputValidator.validatePagination(-1, 20));
        }

        @Test
        @DisplayName("Should reject non-positive page size")
        void validatePagination_NonPositiveSize() {
            assertThrows(InvalidInputException.class,
                    () -> InputValidator.validatePagination(0, 0));
        }
    }

    @Nested
    @DisplayName("Metadata Sanitization")
    class MetadataSanitizationTests {

        @Test
        @DisplayName("Should return null for null input")
        void sanitizeMetadata_Null() {
            assertNull(InputValidator.sanitizeMetadata(null));
        }

        @Test
        @DisplayName("Should remove control characters")
        void sanitizeMetadata_RemovesControlChars() {
            String input = "test\u0000data\u001Fwith\u007Fcontrol";
            String result = InputValidator.sanitizeMetadata(input);
            assertEquals("testdatawithcontrol", result);
        }

        @Test
        @DisplayName("Should truncate long metadata")
        void sanitizeMetadata_Truncates() {
            String longInput = "a".repeat(5000);
            String result = InputValidator.sanitizeMetadata(longInput);
            assertEquals(4096, result.length());
        }

        @Test
        @DisplayName("Should preserve valid metadata")
        void sanitizeMetadata_PreservesValid() {
            String input = "{\"key\": \"value\", \"number\": 123}";
            String result = InputValidator.sanitizeMetadata(input);
            assertEquals(input, result);
        }
    }
}
