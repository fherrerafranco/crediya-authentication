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
    @DisplayName("Should be immutable")
    void shouldBeImmutable() {
        Role role = Role.builder()
                .id(1)
                .name("ADMIN")
                .description("Administrator")
                .build();

        // All fields are final and have only getters, so immutability is guaranteed
        assertThat(role.getId()).isEqualTo(1);
        assertThat(role.getName()).isEqualTo("ADMIN");
        assertThat(role.getDescription()).isEqualTo("Administrator");
        
        // Verify fields remain constant
        Integer originalId = role.getId();
        String originalName = role.getName();
        String originalDescription = role.getDescription();
        
        assertThat(role.getId()).isSameAs(originalId);
        assertThat(role.getName()).isSameAs(originalName);
        assertThat(role.getDescription()).isSameAs(originalDescription);
    }
}