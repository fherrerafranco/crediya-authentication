package crediya.authentication.model.role;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTypeTest {

    @Test
    void shouldHaveCorrectEnumValues() {
        // When/Then
        assertEquals(3, RoleType.values().length);
        assertEquals(RoleType.CUSTOMER, RoleType.values()[0]);
        assertEquals(RoleType.ADVISOR, RoleType.values()[1]);
        assertEquals(RoleType.ADMIN, RoleType.values()[2]);
    }

    @Test
    void shouldHaveCorrectIdsAndNames() {
        // Given/When/Then
        assertEquals(Integer.valueOf(1), RoleType.CUSTOMER.getId());
        assertEquals("CUSTOMER", RoleType.CUSTOMER.getName());
        assertEquals("Customer with limited access to own resources", RoleType.CUSTOMER.getDescription());

        assertEquals(Integer.valueOf(2), RoleType.ADVISOR.getId());
        assertEquals("ADVISOR", RoleType.ADVISOR.getName());
        assertEquals("Financial advisor with user management access", RoleType.ADVISOR.getDescription());

        assertEquals(Integer.valueOf(3), RoleType.ADMIN.getId());
        assertEquals("ADMIN", RoleType.ADMIN.getName());
        assertEquals("System administrator with full access", RoleType.ADMIN.getDescription());
    }

    @Test
    void shouldReturnRoleTypeFromValidId() {
        // When/Then
        assertEquals(RoleType.CUSTOMER, RoleType.fromId(1));
        assertEquals(RoleType.ADVISOR, RoleType.fromId(2));
        assertEquals(RoleType.ADMIN, RoleType.fromId(3));
    }

    @Test
    void shouldReturnNullForNullId() {
        // When/Then
        assertNull(RoleType.fromId(null));
    }

    @Test
    void shouldThrowExceptionForInvalidId() {
        // When/Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> RoleType.fromId(999)
        );
        assertTrue(exception.getMessage().contains("Invalid role ID: 999"));
    }

    @Test
    void shouldReturnRoleTypeFromValidName() {
        // When/Then
        assertEquals(RoleType.ADMIN, RoleType.fromName("ADMIN"));
        assertEquals(RoleType.ADVISOR, RoleType.fromName("ADVISOR"));
        assertEquals(RoleType.CUSTOMER, RoleType.fromName("CUSTOMER"));
    }

    @Test
    void shouldReturnRoleTypeFromNameCaseInsensitive() {
        // When/Then
        assertEquals(RoleType.ADMIN, RoleType.fromName("admin"));
        assertEquals(RoleType.ADVISOR, RoleType.fromName("Advisor"));
        assertEquals(RoleType.CUSTOMER, RoleType.fromName("CUSTOMER"));
    }

    @Test
    void shouldReturnRoleTypeFromNameWithWhitespace() {
        // When/Then
        assertEquals(RoleType.ADMIN, RoleType.fromName(" ADMIN "));
        assertEquals(RoleType.ADVISOR, RoleType.fromName("\tADVISOR\n"));
        assertEquals(RoleType.CUSTOMER, RoleType.fromName("  customer  "));
    }

    @Test
    void shouldReturnNullForNullName() {
        // When/Then
        assertNull(RoleType.fromName(null));
    }

    @Test
    void shouldReturnNullForEmptyName() {
        // When/Then
        assertNull(RoleType.fromName(""));
    }

    @Test
    void shouldReturnNullForBlankName() {
        // When/Then
        assertNull(RoleType.fromName("   "));
    }

    @Test
    void shouldThrowExceptionForInvalidName() {
        // When/Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> RoleType.fromName("INVALID_ROLE")
        );
        assertTrue(exception.getMessage().contains("Invalid role name: INVALID_ROLE"));
    }

    @Test
    void shouldIdentifyAdminRole() {
        // When/Then
        assertTrue(RoleType.ADMIN.isAdmin());
        assertFalse(RoleType.ADVISOR.isAdmin());
        assertFalse(RoleType.CUSTOMER.isAdmin());
    }

    @Test
    void shouldIdentifyAdvisorRole() {
        // When/Then
        assertFalse(RoleType.ADMIN.isAdvisor());
        assertTrue(RoleType.ADVISOR.isAdvisor());
        assertFalse(RoleType.CUSTOMER.isAdvisor());
    }

    @Test
    void shouldIdentifyCustomerRole() {
        // When/Then
        assertFalse(RoleType.ADMIN.isCustomer());
        assertFalse(RoleType.ADVISOR.isCustomer());
        assertTrue(RoleType.CUSTOMER.isCustomer());
    }

    @Test
    void shouldIdentifyAdministrativeAccess() {
        // When/Then
        assertTrue(RoleType.ADMIN.hasAdministrativeAccess());
        assertTrue(RoleType.ADVISOR.hasAdministrativeAccess());
        assertFalse(RoleType.CUSTOMER.hasAdministrativeAccess());
    }
}