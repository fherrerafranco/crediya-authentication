package crediya.authentication.model.auth;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AuthenticationResultTest {

    @Test
    void shouldCreateAuthenticationResultWithAllFields() {
        // Given
        String token = "jwt-token-123";
        String userId = "user-123";
        Integer roleId = 1;
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1);

        // When
        AuthenticationResult result = AuthenticationResult.builder()
                .token(token)
                .userId(userId)
                .roleId(roleId)
                .expiresAt(expiresAt)
                .build();

        // Then
        assertEquals(token, result.getToken());
        assertEquals(userId, result.getUserId());
        assertEquals(roleId, result.getRoleId());
        assertEquals(expiresAt, result.getExpiresAt());
    }

    @Test
    void shouldCreateAuthenticationResultWithNullValues() {
        // When
        AuthenticationResult result = AuthenticationResult.builder()
                .token(null)
                .userId(null)
                .roleId(null)
                .expiresAt(null)
                .build();

        // Then
        assertNull(result.getToken());
        assertNull(result.getUserId());
        assertNull(result.getRoleId());
        assertNull(result.getExpiresAt());
    }

    @Test
    void shouldCreateAuthenticationResultWithMinimalFields() {
        // Given
        String token = "token";
        String userId = "user";

        // When
        AuthenticationResult result = AuthenticationResult.builder()
                .token(token)
                .userId(userId)
                .build();

        // Then
        assertEquals(token, result.getToken());
        assertEquals(userId, result.getUserId());
        assertNull(result.getRoleId());
        assertNull(result.getExpiresAt());
    }
}