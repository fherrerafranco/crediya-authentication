package crediya.authentication.api.exception;

import crediya.authentication.api.constants.ErrorMessages;
import crediya.authentication.api.constants.LogMessages;
import crediya.authentication.model.exception.BusinessRuleViolationException;
import crediya.authentication.model.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidationExceptions(WebExchangeBindException ex) {
        log.error(LogMessages.VALIDATION_ERROR, "Request validation failed with {} field errors", ex.getFieldErrorCount());
        
        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value"
                ));

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", ErrorMessages.VALIDATION_FAILED);
        errorResponse.put("message", LogMessages.VALIDATION_FAILED_MESSAGE);
        errorResponse.put("fieldErrors", fieldErrors);

        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error(LogMessages.DATA_INTEGRITY_VIOLATION, ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        
        String exceptionMessage = ex.getMessage() != null ? ex.getMessage().toLowerCase() : "";
        
        if (exceptionMessage.contains("not-null constraint") && exceptionMessage.contains("user_id")) {
            errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            errorResponse.put("error", ErrorMessages.INTERNAL_SERVER_ERROR);
            errorResponse.put("message", LogMessages.UNEXPECTED_ERROR_MESSAGE);
            return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
        } else if (exceptionMessage.contains("duplicate key value") && exceptionMessage.contains("email")) {
            errorResponse.put("status", HttpStatus.CONFLICT.value());
            errorResponse.put("error", "Conflict");
            errorResponse.put("message", LogMessages.EMAIL_ALREADY_REGISTERED);
            return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse));
        } else {
            errorResponse.put("status", HttpStatus.CONFLICT.value());
            errorResponse.put("error", "Conflict");
            errorResponse.put("message", LogMessages.DATA_CONFLICT_OCCURRED);
            return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse));
        }
    }

    @ExceptionHandler(ValidationException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleDomainValidationException(ValidationException ex) {
        log.error(LogMessages.BUSINESS_VALIDATION_ERROR, ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", ErrorMessages.BAD_REQUEST);
        errorResponse.put("message", ex.getMessage());

        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleBusinessRuleViolation(BusinessRuleViolationException ex) {
        log.error(LogMessages.BUSINESS_VALIDATION_ERROR, ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.CONFLICT.value());
        errorResponse.put("error", "Conflict");
        errorResponse.put("message", ex.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleValidationException(IllegalArgumentException ex) {
        log.error(LogMessages.BUSINESS_VALIDATION_ERROR, ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", ex.getMessage());

        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleNoResourceFoundException(NoResourceFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.NOT_FOUND.value());
        errorResponse.put("error", "Not Found");
        errorResponse.put("message", ErrorMessages.RESOURCE_NOT_FOUND);
        errorResponse.put("details", ex.getMessage());

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Map<String, Object>>> handleGenericException(Exception ex) {
        // Avoid calling getMessage() on WebExchangeBindException as it requires MethodParameter
        if (ex instanceof WebExchangeBindException) {
            String errorMessage = "Validation error with " + ((WebExchangeBindException) ex).getFieldErrorCount() + " field errors";
            log.error(LogMessages.UNEXPECTED_ERROR, errorMessage);
        } else if (ex instanceof RuntimeException && ex.getMessage() != null) {
            // For runtime exceptions in tests, log without stack trace to reduce noise
            log.error(LogMessages.UNEXPECTED_ERROR, ex.getMessage());
        } else {
            log.error(LogMessages.UNEXPECTED_ERROR, ex.getMessage(), ex);
        }
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", LogMessages.UNEXPECTED_ERROR_MESSAGE);

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
}