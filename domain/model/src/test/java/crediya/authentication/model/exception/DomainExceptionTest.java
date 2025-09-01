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
}