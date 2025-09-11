package crediya.authentication.model.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizationResultTest {

    @Test
    void shouldCreateAuthorizedResult() {
        // Given
        Permission permission = Permission.VIEW_ALL_USERS;

        // When
        AuthorizationResult result = AuthorizationResult.authorized(permission);

        // Then
        assertTrue(result.isAuthorized());
        assertFalse(result.isDenied());
        assertEquals(permission, result.getPermission());
        assertNull(result.getReason());
    }

    @Test
    void shouldCreateDeniedResultWithReason() {
        // Given
        Permission permission = Permission.DELETE_USER;
        String reason = "Insufficient privileges";

        // When
        AuthorizationResult result = AuthorizationResult.denied(permission, reason);

        // Then
        assertFalse(result.isAuthorized());
        assertTrue(result.isDenied());
        assertEquals(permission, result.getPermission());
        assertEquals(reason, result.getReason());
    }

    @Test
    void shouldCreateDeniedResultWithDefaultReason() {
        // Given
        Permission permission = Permission.CREATE_USER;

        // When
        AuthorizationResult result = AuthorizationResult.denied(permission);

        // Then
        assertFalse(result.isAuthorized());
        assertTrue(result.isDenied());
        assertEquals(permission, result.getPermission());
        assertEquals("Access denied for permission: CREATE_USER", result.getReason());
    }

    @Test
    void shouldCreateResultUsingBuilder() {
        // Given
        Permission permission = Permission.UPDATE_USER;
        String reason = "Custom reason";

        // When
        AuthorizationResult result = AuthorizationResult.builder()
                .authorized(true)
                .permission(permission)
                .reason(reason)
                .build();

        // Then
        assertTrue(result.isAuthorized());
        assertFalse(result.isDenied());
        assertEquals(permission, result.getPermission());
        assertEquals(reason, result.getReason());
    }

    @Test
    void shouldCreateResultWithNullPermission() {
        // When
        AuthorizationResult result = AuthorizationResult.authorized(null);

        // Then
        assertTrue(result.isAuthorized());
        assertNull(result.getPermission());
    }

    @Test
    void shouldCreateDeniedResultWithNullReasonAndPermission() {
        // When
        AuthorizationResult result = AuthorizationResult.denied(null, null);

        // Then
        assertFalse(result.isAuthorized());
        assertTrue(result.isDenied());
        assertNull(result.getPermission());
        assertNull(result.getReason());
    }
}