package crediya.authentication.model.role;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

class RoleTest {

    @Test
    @DisplayName("Should create role with builder")
    void shouldCreateRoleWithBuilder() {
        Role role = Role.builder()
                .id(1)
                .name("ADMIN")
                .description("Administrator role with full access")
                .build();

        assertThat(role).isNotNull();
        assertThat(role.getId()).isEqualTo(1);
        assertThat(role.getName()).isEqualTo("ADMIN");
        assertThat(role.getDescription()).isEqualTo("Administrator role with full access");
    }

    @Test
    @DisplayName("Should create role with all args constructor")
    void shouldCreateRoleWithAllArgsConstructor() {
        Role role = new Role(2, "ADVISOR", "Financial advisor role");

        assertThat(role).isNotNull();
        assertThat(role.getId()).isEqualTo(2);
        assertThat(role.getName()).isEqualTo("ADVISOR");
        assertThat(role.getDescription()).isEqualTo("Financial advisor role");
    }

    @Test
    @DisplayName("Should create role with null values")
    void shouldCreateRoleWithNullValues() {
        Role role = Role.builder().build();

        assertThat(role).isNotNull();
        assertThat(role.getId()).isNull();
        assertThat(role.getName()).isNull();
        assertThat(role.getDescription()).isNull();
    }

    @Test
    @DisplayName("Should create role with partial data")
    void shouldCreateRoleWithPartialData() {
        Role role = Role.builder()
                .id(3)
                .name("CUSTOMER")
                .build();

        assertThat(role).isNotNull();
        assertThat(role.getId()).isEqualTo(3);
        assertThat(role.getName()).isEqualTo("CUSTOMER");
        assertThat(role.getDescription()).isNull();
    }


    @Test
    @DisplayName("Should have proper object identity")
    void shouldHaveProperObjectIdentity() {
        Role role1 = Role.builder()
                .id(1)
                .name("ADMIN")
                .description("Administrator")
                .build();

        Role role2 = Role.builder()
                .id(2)
                .name("CUSTOMER")
                .description("Customer")
                .build();

        assertThat(role1).isNotSameAs(role2);
        assertThat(role1).isSameAs(role1);
        assertThat(role1.getId()).isEqualTo(1);
        assertThat(role2.getId()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should return correct RoleType from name")
    void shouldReturnCorrectRoleTypeFromName() {
        Role adminRole = Role.builder().id(1).name("ADMIN").build();
        Role advisorRole = Role.builder().id(2).name("ADVISOR").build();
        Role customerRole = Role.builder().id(3).name("CUSTOMER").build();

        assertThat(adminRole.getRoleType()).isEqualTo(RoleType.ADMIN);
        assertThat(advisorRole.getRoleType()).isEqualTo(RoleType.ADVISOR);
        assertThat(customerRole.getRoleType()).isEqualTo(RoleType.CUSTOMER);
    }

    @Test
    @DisplayName("Should return permissions for role")
    void shouldReturnPermissionsForRole() {
        Role adminRole = Role.builder().id(1).name("ADMIN").build();
        
        assertThat(adminRole.getPermissions()).isNotEmpty();
    }

    @Test
    @DisplayName("Should check if role has specific permission")
    void shouldCheckIfRoleHasSpecificPermission() {
        Role adminRole = Role.builder().id(1).name("ADMIN").build();
        Role customerRole = Role.builder().id(3).name("CUSTOMER").build();
        
        assertThat(adminRole.hasPermission(crediya.authentication.model.auth.Permission.CREATE_USER)).isTrue();
        assertThat(customerRole.hasPermission(crediya.authentication.model.auth.Permission.CREATE_USER)).isFalse();
    }

    @Test
    @DisplayName("Should identify administrative roles")
    void shouldIdentifyAdministrativeRoles() {
        Role adminRole = Role.builder().id(1).name("ADMIN").build();
        Role advisorRole = Role.builder().id(2).name("ADVISOR").build();
        Role customerRole = Role.builder().id(3).name("CUSTOMER").build();

        assertThat(adminRole.isAdministrative()).isTrue();
        assertThat(advisorRole.isAdministrative()).isTrue();
        assertThat(customerRole.isAdministrative()).isFalse();
    }

    @Test
    @DisplayName("Should create role from RoleType")
    void shouldCreateRoleFromRoleType() {
        Role adminRole = Role.fromRoleType(RoleType.ADMIN);
        
        assertThat(adminRole.getId()).isEqualTo(RoleType.ADMIN.getId());
        assertThat(adminRole.getName()).isEqualTo(RoleType.ADMIN.getName());
        assertThat(adminRole.getDescription()).isEqualTo(RoleType.ADMIN.getDescription());
    }

    @Test
    @DisplayName("Should throw exception when creating role from null RoleType")
    void shouldThrowExceptionWhenCreatingRoleFromNullRoleType() {
        assertThat(org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Role.fromRoleType(null)
        ).getMessage()).contains("RoleType cannot be null");
    }
}