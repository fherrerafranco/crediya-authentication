package crediya.authentication.model.valueobjects;

import crediya.authentication.model.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @Test
    @DisplayName("Should create valid email")
    void shouldCreateValidEmail() {
        String validEmail = "test@example.com";
        Email email = Email.of(validEmail);
        
        assertThat(email.getValue()).isEqualTo(validEmail);
        assertThat(email.toString()).isEqualTo(validEmail);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "user@domain.com",
        "test.email@example.org", 
        "valid_email@test.co.uk",
        "123@numbers.com",
        "user+tag@domain.com"
    })
    @DisplayName("Should accept valid email formats")
    void shouldAcceptValidEmailFormats(String validEmail) {
        Email email = Email.of(validEmail);
        assertThat(email.getValue()).isEqualTo(validEmail);
    }

    @Test
    @DisplayName("Should throw ValidationException for null email")
    void shouldThrowValidationExceptionForNullEmail() {
        assertThatThrownBy(() -> Email.of(null))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("email")
            .hasMessageContaining("cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw ValidationException for empty email")
    void shouldThrowValidationExceptionForEmptyEmail() {
        assertThatThrownBy(() -> Email.of(""))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("email")
            .hasMessageContaining("cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw ValidationException for whitespace-only email")
    void shouldThrowValidationExceptionForWhitespaceOnlyEmail() {
        assertThatThrownBy(() -> Email.of("   "))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("email")
            .hasMessageContaining("cannot be null or empty");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "invalid-email",
        "@domain.com",
        "user@",
        "user@domain",
        "user.domain.com",
        "user@domain@com",
        "user name@domain.com"
    })
    @DisplayName("Should throw ValidationException for invalid email formats")
    void shouldThrowValidationExceptionForInvalidFormats(String invalidEmail) {
        assertThatThrownBy(() -> Email.of(invalidEmail))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("email")
            .hasMessageContaining("invalid email format");
    }

    @Test
    @DisplayName("Should trim whitespace from email")
    void shouldTrimWhitespaceFromEmail() {
        String emailWithSpaces = "  test@example.com  ";
        Email email = Email.of(emailWithSpaces);
        
        assertThat(email.getValue()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should support equals and hashCode")
    void shouldSupportEqualsAndHashCode() {
        Email email1 = Email.of("test@example.com");
        Email email2 = Email.of("test@example.com");
        Email email3 = Email.of("different@example.com");
        
        assertThat(email1).isEqualTo(email2);
        assertThat(email1).isNotEqualTo(email3);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
    }
}