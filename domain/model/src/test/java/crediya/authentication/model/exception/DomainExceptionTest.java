package crediya.authentication.model.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

class DomainExceptionTest {

    @Test
    @DisplayName("Should create BusinessRuleViolationException with message")
    void shouldCreateBusinessRuleViolationExceptionWithMessage() {
        String message = "Business rule violated";
        BusinessRuleViolationException exception = new BusinessRuleViolationException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception).isInstanceOf(DomainException.class);
        assertThat(exception).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should create BusinessRuleViolationException with message and cause")
    void shouldCreateBusinessRuleViolationExceptionWithMessageAndCause() {
        String message = "Business rule violated";
        RuntimeException cause = new RuntimeException("Root cause");
        BusinessRuleViolationException exception = new BusinessRuleViolationException(message, cause);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    @DisplayName("Should create ValidationException with message")
    void shouldCreateValidationExceptionWithMessage() {
        String message = "Validation failed";
        ValidationException exception = new ValidationException(message);
        
        assertThat(exception.getMessage()).isEqualTo(message);
        assertThat(exception).isInstanceOf(BusinessRuleViolationException.class);
        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Should create ValidationException with field and reason")
    void shouldCreateValidationExceptionWithFieldAndReason() {
        String fieldName = "email";
        String reason = "cannot be null";
        ValidationException exception = new ValidationException(fieldName, reason);
        
        assertThat(exception.getMessage()).contains("email");
        assertThat(exception.getMessage()).contains("cannot be null");
        assertThat(exception.getMessage()).contains("Validation failed");
    }

    @Test
    @DisplayName("Should maintain exception hierarchy")
    void shouldMaintainExceptionHierarchy() {
        ValidationException validationException = new ValidationException("test");
        BusinessRuleViolationException businessException = new BusinessRuleViolationException("test");
        
        // ValidationException extends BusinessRuleViolationException
        assertThat(validationException).isInstanceOf(BusinessRuleViolationException.class);
        assertThat(validationException).isInstanceOf(DomainException.class);
        assertThat(validationException).isInstanceOf(RuntimeException.class);
        
        // BusinessRuleViolationException extends DomainException
        assertThat(businessException).isInstanceOf(DomainException.class);
        assertThat(businessException).isInstanceOf(RuntimeException.class);
        
        // BusinessRuleViolationException is not ValidationException
        assertThat(businessException).isNotInstanceOf(ValidationException.class);
    }

    @Test
    @DisplayName("Should create concrete DomainException subclasses with message")
    void shouldCreateConcreteDomainExceptionSubclassesWithMessage() {
        String message = "Domain error occurred";
        BusinessRuleViolationException businessException = new BusinessRuleViolationException(message);
        ValidationException validationException = new ValidationException(message);
        
        assertThat(businessException.getMessage()).isEqualTo(message);
        assertThat(businessException).isInstanceOf(DomainException.class);
        assertThat(businessException).isInstanceOf(RuntimeException.class);
        
        assertThat(validationException.getMessage()).isEqualTo(message);
        assertThat(validationException).isInstanceOf(DomainException.class);
        assertThat(validationException).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should handle null message in exceptions")
    void shouldHandleNullMessageInExceptions() {
        ValidationException validationException = new ValidationException(null);
        BusinessRuleViolationException businessException = new BusinessRuleViolationException(null);
        
        assertThat(validationException.getMessage()).isNull();
        assertThat(businessException.getMessage()).isNull();
    }

    @Test
    @DisplayName("Should handle empty message in exceptions")
    void shouldHandleEmptyMessageInExceptions() {
        String emptyMessage = "";
        ValidationException validationException = new ValidationException(emptyMessage);
        BusinessRuleViolationException businessException = new BusinessRuleViolationException(emptyMessage);
        
        assertThat(validationException.getMessage()).isEmpty();
        assertThat(businessException.getMessage()).isEmpty();
    }

    @Test
    @DisplayName("Should handle very long messages")
    void shouldHandleVeryLongMessages() {
        String longMessage = "Error: " + "a".repeat(10000);
        ValidationException validationException = new ValidationException(longMessage);
        BusinessRuleViolationException businessException = new BusinessRuleViolationException(longMessage);
        
        assertThat(validationException.getMessage()).isEqualTo(longMessage);
        assertThat(businessException.getMessage()).isEqualTo(longMessage);
    }

    @Test
    @DisplayName("Should handle special characters in messages")
    void shouldHandleSpecialCharactersInMessages() {
        String specialMessage = "Error: áéíóú ñÑ @#$%^&*()_+-=[]{}|;':\",./<>?";
        ValidationException validationException = new ValidationException(specialMessage);
        BusinessRuleViolationException businessException = new BusinessRuleViolationException(specialMessage);
        
        assertThat(validationException.getMessage()).isEqualTo(specialMessage);
        assertThat(businessException.getMessage()).isEqualTo(specialMessage);
    }

    @Test
    @DisplayName("Should handle whitespace-only messages")
    void shouldHandleWhitespaceOnlyMessages() {
        String whitespaceMessage = "   \t\n\r   ";
        ValidationException validationException = new ValidationException(whitespaceMessage);
        BusinessRuleViolationException businessException = new BusinessRuleViolationException(whitespaceMessage);
        
        assertThat(validationException.getMessage()).isEqualTo(whitespaceMessage);
        assertThat(businessException.getMessage()).isEqualTo(whitespaceMessage);
    }

    @Test
    @DisplayName("Should chain BusinessRuleViolationExceptions correctly")
    void shouldChainBusinessRuleViolationExceptionsCorrectly() {
        RuntimeException rootCause = new RuntimeException("Root cause");
        BusinessRuleViolationException middleCause = new BusinessRuleViolationException("Middle cause", rootCause);
        BusinessRuleViolationException topLevel = new BusinessRuleViolationException("Top level", middleCause);
        
        assertThat(topLevel.getCause()).isEqualTo(middleCause);
        assertThat(middleCause.getCause()).isEqualTo(rootCause);
        assertThat(rootCause.getCause()).isNull();
    }

    @Test
    @DisplayName("Should support toString method")
    void shouldSupportToStringMethod() {
        ValidationException exception = new ValidationException("test message");
        String toString = exception.toString();
        
        assertThat(toString).contains("ValidationException");
        assertThat(toString).contains("test message");
    }

    @Test
    @DisplayName("Should support printStackTrace without errors")
    void shouldSupportPrintStackTraceWithoutErrors() {
        ValidationException exception = new ValidationException("test");
        
        // This should not throw any exception
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream ps = new java.io.PrintStream(baos);
        exception.printStackTrace(ps);
        
        String stackTrace = baos.toString();
        assertThat(stackTrace).contains("ValidationException");
        assertThat(stackTrace).contains("test");
    }

    @Test
    @DisplayName("Should create ValidationException from formatted template")
    void shouldCreateValidationExceptionFromFormattedTemplate() {
        String fieldName = "testField";
        String reason = "test reason";
        ValidationException exception = new ValidationException(fieldName, reason);
        
        assertThat(exception.getMessage()).contains("Validation failed for field 'testField': test reason");
        assertThat(exception).isInstanceOf(BusinessRuleViolationException.class);
        assertThat(exception).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("Should handle null field and reason in ValidationException")
    void shouldHandleNullFieldAndReasonInValidationException() {
        ValidationException exception = new ValidationException(null, null);
        
        assertThat(exception.getMessage()).contains("Validation failed for field 'null': null");
    }

    @Test
    @DisplayName("Should handle empty field and reason in ValidationException")
    void shouldHandleEmptyFieldAndReasonInValidationException() {
        ValidationException exception = new ValidationException("", "");
        
        assertThat(exception.getMessage()).contains("Validation failed for field '': ");
    }
}