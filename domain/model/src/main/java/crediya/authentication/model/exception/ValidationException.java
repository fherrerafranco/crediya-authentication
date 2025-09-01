package crediya.authentication.model.exception;

import crediya.authentication.model.constants.DomainErrorMessages;

public class ValidationException extends BusinessRuleViolationException {
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String fieldName, String reason) {
        super(String.format(DomainErrorMessages.VALIDATION_FAILED_TEMPLATE, fieldName, reason));
    }
}