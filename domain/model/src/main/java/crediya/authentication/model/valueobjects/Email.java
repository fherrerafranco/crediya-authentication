package crediya.authentication.model.valueobjects;

import crediya.authentication.model.constants.DomainErrorMessages;
import crediya.authentication.model.exception.ValidationException;
import java.util.Objects;
import java.util.regex.Pattern;

public class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    private final String value;
    
    private Email(String value) {
        this.value = value;
    }
    
    public static Email of(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("email", DomainErrorMessages.EMAIL_NULL_OR_EMPTY);
        }
        
        String trimmedEmail = email.trim();
        if (!EMAIL_PATTERN.matcher(trimmedEmail).matches()) {
            throw new ValidationException("email", DomainErrorMessages.EMAIL_INVALID_FORMAT + ": " + trimmedEmail);
        }
        
        return new Email(trimmedEmail);
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}