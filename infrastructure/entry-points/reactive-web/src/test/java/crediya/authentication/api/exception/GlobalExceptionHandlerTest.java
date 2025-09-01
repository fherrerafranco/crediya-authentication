package crediya.authentication.api.exception;

import crediya.authentication.model.exception.BusinessRuleViolationException;
import crediya.authentication.model.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("Should handle ValidationException with 400 Bad Request")
    void shouldHandleValidationExceptionWith400BadRequest() {
        ValidationException exception = new ValidationException("email", "cannot be null");
        
        Mono<ResponseEntity<Map<String, Object>>> result = 
            globalExceptionHandler.handleDomainValidationException(exception);
        
        StepVerifier.create(result)
            .assertNext(response -> {
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().get("status")).isEqualTo(400);
                assertThat(response.getBody().get("error")).isEqualTo("Bad Request");
                assertThat(response.getBody().get("message")).asString().contains("email");
                assertThat(response.getBody().get("timestamp")).isInstanceOf(LocalDateTime.class);
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("Should handle BusinessRuleViolationException with 409 Conflict")
    void shouldHandleBusinessRuleViolationExceptionWith409Conflict() {
        BusinessRuleViolationException exception = 
            new BusinessRuleViolationException("Email already registered: test@example.com");
        
        Mono<ResponseEntity<Map<String, Object>>> result = 
            globalExceptionHandler.handleBusinessRuleViolation(exception);
        
        StepVerifier.create(result)
            .assertNext(response -> {
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().get("status")).isEqualTo(409);
                assertThat(response.getBody().get("error")).isEqualTo("Conflict");
                assertThat(response.getBody().get("message")).asString().contains("Email already registered");
                assertThat(response.getBody().get("timestamp")).isInstanceOf(LocalDateTime.class);
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("Should handle WebExchangeBindException with 400 Bad Request")
    void shouldHandleWebExchangeBindExceptionWith400BadRequest() {
        Object target = new Object();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(target, "test");
        bindingResult.addError(new FieldError("test", "firstName", "First name is required"));
        bindingResult.addError(new FieldError("test", "email", "Email must be valid"));
        
        WebExchangeBindException exception = new WebExchangeBindException(null, bindingResult);
        
        Mono<ResponseEntity<Map<String, Object>>> result = 
            globalExceptionHandler.handleValidationExceptions(exception);
        
        StepVerifier.create(result)
            .assertNext(response -> {
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().get("status")).isEqualTo(400);
                assertThat(response.getBody().get("error")).isEqualTo("Validation Failed");
                assertThat(response.getBody().get("fieldErrors")).isInstanceOf(Map.class);
                
                @SuppressWarnings("unchecked")
                Map<String, String> fieldErrors = (Map<String, String>) response.getBody().get("fieldErrors");
                assertThat(fieldErrors).containsKey("firstName");
                assertThat(fieldErrors).containsKey("email");
                assertThat(fieldErrors.get("firstName")).isEqualTo("First name is required");
                assertThat(fieldErrors.get("email")).isEqualTo("Email must be valid");
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("Should handle DataIntegrityViolationException with 409 Conflict")
    void shouldHandleDataIntegrityViolationExceptionWith409Conflict() {
        DataIntegrityViolationException exception = 
            new DataIntegrityViolationException("Duplicate entry for email");
        
        Mono<ResponseEntity<Map<String, Object>>> result = 
            globalExceptionHandler.handleDataIntegrityViolation(exception);
        
        StepVerifier.create(result)
            .assertNext(response -> {
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().get("status")).isEqualTo(409);
                assertThat(response.getBody().get("error")).isEqualTo("Conflict");
                assertThat(response.getBody().get("timestamp")).isInstanceOf(LocalDateTime.class);
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("Should handle IllegalArgumentException with 400 Bad Request")
    void shouldHandleIllegalArgumentExceptionWith400BadRequest() {
        IllegalArgumentException exception = new IllegalArgumentException("Invalid argument");
        
        Mono<ResponseEntity<Map<String, Object>>> result = 
            globalExceptionHandler.handleValidationException(exception);
        
        StepVerifier.create(result)
            .assertNext(response -> {
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().get("status")).isEqualTo(400);
                assertThat(response.getBody().get("error")).isEqualTo("Bad Request");
                assertThat(response.getBody().get("message")).isEqualTo("Invalid argument");
                assertThat(response.getBody().get("timestamp")).isInstanceOf(LocalDateTime.class);
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("Should handle NoResourceFoundException with 404 Not Found")
    void shouldHandleNoResourceFoundExceptionWith404NotFound() {
        NoResourceFoundException exception = new NoResourceFoundException("api/v1/invalid-path");
        
        Mono<ResponseEntity<Map<String, Object>>> result = 
            globalExceptionHandler.handleNoResourceFoundException(exception);
        
        StepVerifier.create(result)
            .assertNext(response -> {
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().get("status")).isEqualTo(404);
                assertThat(response.getBody().get("error")).isEqualTo("Not Found");
                assertThat(response.getBody().get("message")).isEqualTo("The requested resource was not found");
                assertThat(response.getBody().get("details")).asString().contains("api/v1/invalid-path");
                assertThat(response.getBody().get("timestamp")).isInstanceOf(LocalDateTime.class);
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("Should handle generic Exception with 500 Internal Server Error")
    void shouldHandleGenericExceptionWith500InternalServerError() {
        Exception exception = new RuntimeException("Unexpected error");
        
        Mono<ResponseEntity<Map<String, Object>>> result = 
            globalExceptionHandler.handleGenericException(exception);
        
        StepVerifier.create(result)
            .assertNext(response -> {
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                assertThat(response.getBody()).isNotNull();
                assertThat(response.getBody().get("status")).isEqualTo(500);
                assertThat(response.getBody().get("error")).isEqualTo("Internal Server Error");
                assertThat(response.getBody().get("message")).asString().contains("An unexpected error occurred");
                assertThat(response.getBody().get("timestamp")).isInstanceOf(LocalDateTime.class);
            })
            .verifyComplete();
    }

    @Test
    @DisplayName("Should prioritize ValidationException over BusinessRuleViolationException")
    void shouldPrioritizeValidationExceptionOverBusinessRuleViolationException() {
        // ValidationException extends BusinessRuleViolationException
        // More specific handler should be called (ValidationException -> 400)
        // Not BusinessRuleViolationException -> 409
        
        ValidationException exception = new ValidationException("Field validation failed");
        
        Mono<ResponseEntity<Map<String, Object>>> result = 
            globalExceptionHandler.handleDomainValidationException(exception);
        
        StepVerifier.create(result)
            .assertNext(response -> {
                // Should be 400 (ValidationException) not 409 (BusinessRuleViolationException)
                assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                assertThat(response.getBody().get("status")).isEqualTo(400);
            })
            .verifyComplete();
    }
}