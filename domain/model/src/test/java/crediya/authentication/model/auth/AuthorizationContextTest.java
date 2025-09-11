package crediya.authentication.model.auth;

import crediya.authentication.model.role.RoleType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AuthorizationContextTest {

    @Test
    void shouldCreateAuthorizationContextWithAllFields() {
        // Given
        String userId = "user-123";
        RoleType roleType = RoleType.ADMIN;
        String targetResourceId = "resource-456";
        String clientIp = "192.168.1.1";

        // When
        AuthorizationContext context = AuthorizationContext.builder()
                .userId(userId)
                .roleType(roleType)
                .targetResourceId(targetResourceId)
                .clientIp(clientIp)
                .build();

        // Then
        assertEquals(userId, context.getUserId());
        assertEquals(roleType, context.getRoleType());
        assertEquals(targetResourceId, context.getTargetResourceId());
        assertEquals(clientIp, context.getClientIp());
    }

    @Test
    void shouldReturnTrueWhenUserHasExpectedRole() {
        // Given
        AuthorizationContext context = AuthorizationContext.builder()
                .userId("user-123")
                .roleType(RoleType.ADMIN)
                .build();

        // When/Then
        assertTrue(context.hasRole(RoleType.ADMIN));
        assertFalse(context.hasRole(RoleType.ADVISOR));
    }

    @Test
    void shouldReturnFalseWhenRoleTypeIsNull() {
        // Given
        AuthorizationContext context = AuthorizationContext.builder()
                .userId("user-123")
                .roleType(null)
                .build();

        // When/Then
        assertFalse(context.hasRole(RoleType.ADMIN));
    }

    @Test
    void shouldReturnTrueWhenUserHasAnyOfExpectedRoles() {
        // Given
        AuthorizationContext context = AuthorizationContext.builder()
                .userId("user-123")
                .roleType(RoleType.ADVISOR)
                .build();

        // When/Then
        assertTrue(context.hasAnyRole(RoleType.ADMIN, RoleType.ADVISOR));
        assertFalse(context.hasAnyRole(RoleType.ADMIN, RoleType.CUSTOMER));
    }

    @Test
    void shouldReturnFalseWhenRoleTypeIsNullForHasAnyRole() {
        // Given
        AuthorizationContext context = AuthorizationContext.builder()
                .userId("user-123")
                .roleType(null)
                .build();

        // When/Then
        assertFalse(context.hasAnyRole(RoleType.ADMIN, RoleType.ADVISOR));
    }

    @Test
    void shouldReturnFalseWhenExpectedRolesAreNull() {
        // Given
        AuthorizationContext context = AuthorizationContext.builder()
                .userId("user-123")
                .roleType(RoleType.ADMIN)
                .build();

        // When/Then
        assertFalse(context.hasAnyRole((RoleType[]) null));
    }

    @Test
    void shouldReturnTrueWhenUserIsOwnerOfResource() {
        // Given
        String userId = "user-123";
        AuthorizationContext context = AuthorizationContext.builder()
                .userId(userId)
                .roleType(RoleType.CUSTOMER)
                .build();

        // When/Then
        assertTrue(context.isOwnerOf(userId));
        assertFalse(context.isOwnerOf("different-user"));
    }

    @Test
    void shouldReturnFalseWhenUserIdIsNullForOwnership() {
        // Given
        AuthorizationContext context = AuthorizationContext.builder()
                .userId(null)
                .roleType(RoleType.CUSTOMER)
                .build();

        // When/Then
        assertFalse(context.isOwnerOf("user-123"));
    }

    @Test
    void shouldReturnTrueWhenRoleHasAdministrativeAccess() {
        // Given
        AuthorizationContext context = AuthorizationContext.builder()
                .userId("user-123")
                .roleType(RoleType.ADMIN)
                .build();

        // When/Then
        assertTrue(context.isAdministrative());
    }

    @Test
    void shouldReturnFalseWhenRoleTypeIsNullForAdministrative() {
        // Given
        AuthorizationContext context = AuthorizationContext.builder()
                .userId("user-123")
                .roleType(null)
                .build();

        // When/Then
        assertFalse(context.isAdministrative());
    }

    @Test
    void shouldReturnTrueWhenUserCanAccessResourceAsAdmin() {
        // Given
        AuthorizationContext context = AuthorizationContext.builder()
                .userId("admin-user")
                .roleType(RoleType.ADMIN)
                .build();

        // When/Then
        assertTrue(context.canAccessResource("any-resource"));
    }

    @Test
    void shouldReturnTrueWhenUserCanAccessResourceAsOwner() {
        // Given
        String userId = "user-123";
        AuthorizationContext context = AuthorizationContext.builder()
                .userId(userId)
                .roleType(RoleType.CUSTOMER)
                .build();

        // When/Then
        assertTrue(context.canAccessResource(userId));
        assertFalse(context.canAccessResource("different-resource"));
    }

    @Test
    void shouldCreateContextUsingStaticFactoryMethod() {
        // Given
        String userId = "user-123";
        RoleType roleType = RoleType.ADVISOR;

        // When
        AuthorizationContext context = AuthorizationContext.of(userId, roleType);

        // Then
        assertEquals(userId, context.getUserId());
        assertEquals(roleType, context.getRoleType());
        assertNull(context.getTargetResourceId());
        assertNull(context.getClientIp());
    }

    @Test
    void shouldCreateContextWithTargetResourceUsingStaticFactoryMethod() {
        // Given
        String userId = "user-123";
        RoleType roleType = RoleType.ADVISOR;
        String targetResourceId = "resource-456";

        // When
        AuthorizationContext context = AuthorizationContext.of(userId, roleType, targetResourceId);

        // Then
        assertEquals(userId, context.getUserId());
        assertEquals(roleType, context.getRoleType());
        assertEquals(targetResourceId, context.getTargetResourceId());
        assertNull(context.getClientIp());
    }
}