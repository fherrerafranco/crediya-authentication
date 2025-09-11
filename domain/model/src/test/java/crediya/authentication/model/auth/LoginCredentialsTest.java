package crediya.authentication.model.auth;

import crediya.authentication.model.valueobjects.Email;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginCredentialsTest {

    @Test
    void shouldCreateLoginCredentialsWithValidEmailAndPassword() {
        // Given
        Email email = Email.of("user@example.com");
        String password = "password123";

        // When
        LoginCredentials credentials = new LoginCredentials(email, password);

        // Then
        assertEquals(email, credentials.getEmail());
        assertEquals(password, credentials.getPassword());
    }

    @Test
    void shouldCreateLoginCredentialsWithNullEmail() {
        // Given
        String password = "password123";

        // When
        LoginCredentials credentials = new LoginCredentials(null, password);

        // Then
        assertNull(credentials.getEmail());
        assertEquals(password, credentials.getPassword());
    }

    @Test
    void shouldCreateLoginCredentialsWithNullPassword() {
        // Given
        Email email = Email.of("user@example.com");

        // When
        LoginCredentials credentials = new LoginCredentials(email, null);

        // Then
        assertEquals(email, credentials.getEmail());
        assertNull(credentials.getPassword());
    }

    @Test
    void shouldCreateLoginCredentialsWithEmptyPassword() {
        // Given
        Email email = Email.of("user@example.com");
        String password = "";

        // When
        LoginCredentials credentials = new LoginCredentials(email, password);

        // Then
        assertEquals(email, credentials.getEmail());
        assertEquals(password, credentials.getPassword());
    }
}