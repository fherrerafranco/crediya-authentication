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
                .roleId("1")
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
        assertThat(user.getRoleId()).isEqualTo("1");
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
                "2",
Salary.of(new BigDecimal("75000")),
                "1985-05-15",
                "456 Oak Ave"
        );

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEqualTo("987654321");
        assertThat(user.getFirstName()).isEqualTo("Jane");
        assertThat(user.getLastName()).isEqualTo("Smith");
assertThat(user.getEmail()).isEqualTo(Email.of("jane.smith@example.com"));
        assertThat(user.getIdentityDocument()).isEqualTo("987654321");
        assertThat(user.getPhone()).isEqualTo("0987654321");
        assertThat(user.getRoleId()).isEqualTo("2");
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
                .roleId("1")
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

    @Test
    @DisplayName("Should have toString method from Object class")
    void shouldHaveToStringMethodFromObjectClass() {
        User user = User.builder()
                .id("123")
                .firstName("John")
                .lastName("Doe")
                .email(Email.of("john@example.com"))
                .build();

        String userString = user.toString();
        // Default Object.toString() format: ClassName@hashcode
        assertThat(userString).isNotNull();
        assertThat(userString).isNotEmpty();
        assertThat(userString).contains("User@");
    }

    @Test
    @DisplayName("Should handle empty strings as fields")
    void shouldHandleEmptyStringsAsFields() {
        User user = User.builder()
                .id("")
                .firstName("")
                .lastName("")
                .identityDocument("")
                .phone("")
                .roleId("")
                .birthDate("")
                .address("")
                .build();

        assertThat(user).isNotNull();
        assertThat(user.getId()).isEmpty();
        assertThat(user.getFirstName()).isEmpty();
        assertThat(user.getLastName()).isEmpty();
        assertThat(user.getIdentityDocument()).isEmpty();
        assertThat(user.getPhone()).isEmpty();
        assertThat(user.getRoleId()).isEmpty();
        assertThat(user.getBirthDate()).isEmpty();
        assertThat(user.getAddress()).isEmpty();
    }

    @Test
    @DisplayName("Should handle very long strings")
    void shouldHandleVeryLongStrings() {
        String longString = "a".repeat(1000);
        
        User user = User.builder()
                .firstName(longString)
                .lastName(longString)
                .identityDocument(longString)
                .phone(longString)
                .address(longString)
                .build();

        assertThat(user.getFirstName()).hasSize(1000);
        assertThat(user.getLastName()).hasSize(1000);
        assertThat(user.getIdentityDocument()).hasSize(1000);
        assertThat(user.getPhone()).hasSize(1000);
        assertThat(user.getAddress()).hasSize(1000);
    }

    @Test
    @DisplayName("Should handle special characters in fields")
    void shouldHandleSpecialCharactersInFields() {
        String specialChars = "áéíóú ñÑ @#$%^&*()_+-=[]{}|;':\",./<>?";
        
        User user = User.builder()
                .firstName(specialChars)
                .lastName(specialChars)
                .identityDocument(specialChars)
                .phone(specialChars)
                .roleId(specialChars)
                .birthDate(specialChars)
                .address(specialChars)
                .build();

        assertThat(user.getFirstName()).isEqualTo(specialChars);
        assertThat(user.getLastName()).isEqualTo(specialChars);
        assertThat(user.getIdentityDocument()).isEqualTo(specialChars);
        assertThat(user.getPhone()).isEqualTo(specialChars);
        assertThat(user.getRoleId()).isEqualTo(specialChars);
        assertThat(user.getBirthDate()).isEqualTo(specialChars);
        assertThat(user.getAddress()).isEqualTo(specialChars);
    }

    @Test
    @DisplayName("Should handle whitespace-only strings")
    void shouldHandleWhitespaceOnlyStrings() {
        String whitespace = "   \t\n\r   ";
        
        User user = User.builder()
                .firstName(whitespace)
                .lastName(whitespace)
                .identityDocument(whitespace)
                .phone(whitespace)
                .roleId(whitespace)
                .birthDate(whitespace)
                .address(whitespace)
                .build();

        assertThat(user.getFirstName()).isEqualTo(whitespace);
        assertThat(user.getLastName()).isEqualTo(whitespace);
        assertThat(user.getIdentityDocument()).isEqualTo(whitespace);
        assertThat(user.getPhone()).isEqualTo(whitespace);
        assertThat(user.getRoleId()).isEqualTo(whitespace);
        assertThat(user.getBirthDate()).isEqualTo(whitespace);
        assertThat(user.getAddress()).isEqualTo(whitespace);
    }

    @Test
    @DisplayName("Should be equal to itself")
    void shouldBeEqualToItself() {
        User user = User.builder()
                .id("123")
                .firstName("John")
                .build();

        assertThat(user).isEqualTo(user);
        assertThat(user.hashCode()).isEqualTo(user.hashCode());
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToNull() {
        User user = User.builder()
                .id("123")
                .firstName("John")
                .build();

        assertThat(user).isNotEqualTo(null);
    }

    @Test
    @DisplayName("Should not be equal to different class")
    void shouldNotBeEqualToDifferentClass() {
        User user = User.builder()
                .id("123")
                .firstName("John")
                .build();

        assertThat(user).isNotEqualTo("not a user");
        assertThat(user).isNotEqualTo(123);
    }
}