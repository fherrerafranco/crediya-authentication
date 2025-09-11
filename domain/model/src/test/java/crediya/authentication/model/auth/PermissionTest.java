package crediya.authentication.model.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PermissionTest {

    @Test
    void shouldReturnCorrectCodeAndDescription() {
        // Given/When/Then
        assertEquals("CREATE_USER", Permission.CREATE_USER.getCode());
        assertEquals("Can create new users", Permission.CREATE_USER.getDescription());
        
        assertEquals("VIEW_ALL_USERS", Permission.VIEW_ALL_USERS.getCode());
        assertEquals("Can view all users in the system", Permission.VIEW_ALL_USERS.getDescription());
        
        assertEquals("UPDATE_USER", Permission.UPDATE_USER.getCode());
        assertEquals("Can update user information", Permission.UPDATE_USER.getDescription());
        
        assertEquals("DELETE_USER", Permission.DELETE_USER.getCode());
        assertEquals("Can delete users", Permission.DELETE_USER.getDescription());
    }

    @Test
    void shouldReturnCorrectLoanPermissions() {
        // Given/When/Then
        assertEquals("CREATE_LOAN_APPLICATION", Permission.CREATE_LOAN_APPLICATION.getCode());
        assertEquals("Can create loan applications", Permission.CREATE_LOAN_APPLICATION.getDescription());
        
        assertEquals("VIEW_OWN_LOAN_APPLICATION", Permission.VIEW_OWN_LOAN_APPLICATION.getCode());
        assertEquals("Can view own loan applications", Permission.VIEW_OWN_LOAN_APPLICATION.getDescription());
        
        assertEquals("VIEW_ALL_LOAN_APPLICATIONS", Permission.VIEW_ALL_LOAN_APPLICATIONS.getCode());
        assertEquals("Can view all loan applications", Permission.VIEW_ALL_LOAN_APPLICATIONS.getDescription());
        
        assertEquals("APPROVE_LOAN_APPLICATION", Permission.APPROVE_LOAN_APPLICATION.getCode());
        assertEquals("Can approve loan applications", Permission.APPROVE_LOAN_APPLICATION.getDescription());
    }

    @Test
    void shouldReturnCorrectSystemPermissions() {
        // Given/When/Then
        assertEquals("VIEW_SYSTEM_HEALTH", Permission.VIEW_SYSTEM_HEALTH.getCode());
        assertEquals("Can view system health information", Permission.VIEW_SYSTEM_HEALTH.getDescription());
        
        assertEquals("MANAGE_SYSTEM_CONFIG", Permission.MANAGE_SYSTEM_CONFIG.getCode());
        assertEquals("Can manage system configuration", Permission.MANAGE_SYSTEM_CONFIG.getDescription());
    }

    @Test
    void shouldReturnPermissionFromValidCode() {
        // Given/When/Then
        assertEquals(Permission.CREATE_USER, Permission.fromCode("CREATE_USER"));
        assertEquals(Permission.VIEW_ALL_USERS, Permission.fromCode("VIEW_ALL_USERS"));
        assertEquals(Permission.UPDATE_USER, Permission.fromCode("UPDATE_USER"));
        assertEquals(Permission.DELETE_USER, Permission.fromCode("DELETE_USER"));
        assertEquals(Permission.CREATE_LOAN_APPLICATION, Permission.fromCode("CREATE_LOAN_APPLICATION"));
        assertEquals(Permission.VIEW_OWN_LOAN_APPLICATION, Permission.fromCode("VIEW_OWN_LOAN_APPLICATION"));
        assertEquals(Permission.VIEW_ALL_LOAN_APPLICATIONS, Permission.fromCode("VIEW_ALL_LOAN_APPLICATIONS"));
        assertEquals(Permission.APPROVE_LOAN_APPLICATION, Permission.fromCode("APPROVE_LOAN_APPLICATION"));
        assertEquals(Permission.VIEW_SYSTEM_HEALTH, Permission.fromCode("VIEW_SYSTEM_HEALTH"));
        assertEquals(Permission.MANAGE_SYSTEM_CONFIG, Permission.fromCode("MANAGE_SYSTEM_CONFIG"));
    }

    @Test
    void shouldReturnPermissionFromCodeWithWhitespace() {
        // Given/When/Then
        assertEquals(Permission.CREATE_USER, Permission.fromCode(" CREATE_USER "));
        assertEquals(Permission.VIEW_ALL_USERS, Permission.fromCode("\tVIEW_ALL_USERS\n"));
    }

    @Test
    void shouldThrowExceptionForNullCode() {
        // When/Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, 
                () -> Permission.fromCode(null)
        );
        
        assertTrue(exception.getMessage().contains("Permission code cannot be null or empty"));
    }

    @Test
    void shouldThrowExceptionForEmptyCode() {
        // When/Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, 
                () -> Permission.fromCode("")
        );
        
        assertTrue(exception.getMessage().contains("Permission code cannot be null or empty"));
    }

    @Test
    void shouldThrowExceptionForBlankCode() {
        // When/Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, 
                () -> Permission.fromCode("   ")
        );
        
        assertTrue(exception.getMessage().contains("Permission code cannot be null or empty"));
    }

    @Test
    void shouldThrowExceptionForInvalidCode() {
        // When/Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, 
                () -> Permission.fromCode("INVALID_PERMISSION")
        );
        
        assertTrue(exception.getMessage().contains("Invalid permission code: INVALID_PERMISSION"));
    }
}