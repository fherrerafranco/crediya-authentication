package crediya.authentication.model.user;

import crediya.authentication.model.valueobjects.Email;
import crediya.authentication.model.valueobjects.Salary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    @DisplayName("Should create user with builder")
    void shouldCreateUserWithBuilder() {
        User user = User.builder()
                .id("123456789")
                .firstName("John")
                .lastName("Doe")
.email(Email.of("john.doe@example.com"))
                .identityDocument("123456789")
                .phone("1234567890")
                .roleId(1)
.baseSalary(Salary.of(new BigDecimal("50000")))
                .birthDate("1990-01-01")
                .address("123 Main St")
                .build();

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo("123456789");
        assertThat(user.getFirstName()).isEqualTo("John");
        assertThat(user.getLastName()).isEqualTo("Doe");
assertThat(user.getEmail()).isEqualTo(Email.of("john.doe@example.com"));
        assertThat(user.getIdentityDocument()).isEqualTo("123456789");
        assertThat(user.getPhone()).isEqualTo("1234567890");
        assertThat(user.getRoleId()).isEqualTo(1);
assertThat(user.getBaseSalary()).isEqualTo(Salary.of(new BigDecimal("50000")));
        assertThat(user.getBirthDate()).isEqualTo("1990-01-01");
        assertThat(user.getAddress()).isEqualTo("123 Main St");
    }

    @Test
    @DisplayName("Should create user with all args constructor")
    void shouldCreateUserWithAllArgsConstructor() {
        User user = new User(
                "987654321",
                "Jane",
                "Smith",
Email.of("jane.smith@example.com"),
                "987654321",
                "0987654321",
                2,
Salary.of(new BigDecimal("75000")),
                "1985-05-15",
                "456 Oak Ave",
                null
        );

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo("987654321");
        assertThat(user.getFirstName()).isEqualTo("Jane");
        assertThat(user.getLastName()).isEqualTo("Smith");
assertThat(user.getEmail()).isEqualTo(Email.of("jane.smith@example.com"));
        assertThat(user.getIdentityDocument()).isEqualTo("987654321");
        assertThat(user.getPhone()).isEqualTo("0987654321");
        assertThat(user.getRoleId()).isEqualTo(2);
assertThat(user.getBaseSalary()).isEqualTo(Salary.of(new BigDecimal("75000")));
        assertThat(user.getBirthDate()).isEqualTo("1985-05-15");
        assertThat(user.getAddress()).isEqualTo("456 Oak Ave");
    }

    @Test
    @DisplayName("Should allow setter modifications")
    void shouldAllowSetterModifications() {
        User user = User.builder()
                .id("111111111")
                .firstName("Original")
                .build();

        user.setFirstName("Modified");
        user.setLastName("Updated");
user.setEmail(Email.of("modified@example.com"));

        assertThat(user.getFirstName()).isEqualTo("Modified");
        assertThat(user.getLastName()).isEqualTo("Updated");
assertThat(user.getEmail()).isEqualTo(Email.of("modified@example.com"));
    }

    @Test
    @DisplayName("Should handle null values")
    void shouldHandleNullValues() {
        User user = User.builder().build();

        assertThat(user).isNotNull();
        assertThat(user.getId()).isNull();
        assertThat(user.getFirstName()).isNull();
        assertThat(user.getLastName()).isNull();
        assertThat(user.getEmail()).isNull();
        assertThat(user.getIdentityDocument()).isNull();
        assertThat(user.getPhone()).isNull();
        assertThat(user.getRoleId()).isNull();
        assertThat(user.getBaseSalary()).isNull();
        assertThat(user.getBirthDate()).isNull();
        assertThat(user.getAddress()).isNull();
    }

    @Test
    @DisplayName("Should create user with partial data")
    void shouldCreateUserWithPartialData() {
        User user = User.builder()
                .id("555555555")
                .firstName("Partial")
.email(Email.of("partial@example.com"))
                .build();

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo("555555555");
        assertThat(user.getFirstName()).isEqualTo("Partial");
assertThat(user.getEmail()).isEqualTo(Email.of("partial@example.com"));
        assertThat(user.getLastName()).isNull();
        assertThat(user.getPhone()).isNull();
    }

    @Test
    @DisplayName("Should have proper object identity")
    void shouldHaveProperObjectIdentity() {
        User user1 = User.builder()
                .id("123")
                .firstName("John")
                .lastName("Doe")
                .email(Email.of("john@example.com"))
                .identityDocument("123456789")
                .phone("1234567890")
                .roleId(1)
                .baseSalary(Salary.of(new BigDecimal("50000")))
                .birthDate("1990-01-01")
                .address("123 Main St")
                .build();

        User user2 = User.builder()
                .id("456")
                .firstName("Jane")
                .lastName("Smith")
                .email(Email.of("jane@example.com"))
                .build();

        // Test that users are different instances
        assertThat(user1).isNotSameAs(user2);
        // Test that user is equal to itself
        assertThat(user1).isSameAs(user1);
        // Test that all properties can be accessed
        assertThat(user1.getId()).isEqualTo("123");
        assertThat(user2.getId()).isEqualTo("456");
    }



}