package crediya.authentication.model.valueobjects;

import crediya.authentication.model.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SalaryTest {

    @Test
    @DisplayName("Should create valid salary")
    void shouldCreateValidSalary() {
        BigDecimal amount = new BigDecimal("50000");
        Salary salary = Salary.of(amount);
        
        assertThat(salary.getValue()).isEqualTo(amount);
        assertThat(salary.toString()).isEqualTo("50000");
    }

    @ParameterizedTest
    @ValueSource(strings = {"0", "1", "1000", "50000", "1000000", "15000000"})
    @DisplayName("Should accept valid salary amounts")
    void shouldAcceptValidSalaryAmounts(String amount) {
        BigDecimal salaryAmount = new BigDecimal(amount);
        Salary salary = Salary.of(salaryAmount);
        
        assertThat(salary.getValue()).isEqualTo(salaryAmount);
    }

    @Test
    @DisplayName("Should throw ValidationException for null salary")
    void shouldThrowValidationExceptionForNullSalary() {
        assertThatThrownBy(() -> Salary.of(null))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("baseSalary")
            .hasMessageContaining("cannot be null");
    }

    @Test
    @DisplayName("Should throw ValidationException for negative salary")
    void shouldThrowValidationExceptionForNegativeSalary() {
        BigDecimal negativeSalary = new BigDecimal("-1000");
        
        assertThatThrownBy(() -> Salary.of(negativeSalary))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("baseSalary")
            .hasMessageContaining("must be at least 0");
    }

    @Test
    @DisplayName("Should throw ValidationException for salary above maximum")
    void shouldThrowValidationExceptionForSalaryAboveMaximum() {
        BigDecimal excessiveSalary = new BigDecimal("15000001");
        
        assertThatThrownBy(() -> Salary.of(excessiveSalary))
            .isInstanceOf(ValidationException.class)
            .hasMessageContaining("baseSalary")
            .hasMessageContaining("must not exceed 15,000,000");
    }

    @Test
    @DisplayName("Should accept minimum salary of zero")
    void shouldAcceptMinimumSalaryOfZero() {
        Salary salary = Salary.of(BigDecimal.ZERO);
        
        assertThat(salary.getValue()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    @DisplayName("Should accept maximum salary")
    void shouldAcceptMaximumSalary() {
        BigDecimal maxSalary = new BigDecimal("15000000");
        Salary salary = Salary.of(maxSalary);
        
        assertThat(salary.getValue()).isEqualTo(maxSalary);
    }

    @Test
    @DisplayName("Should support equals and hashCode")
    void shouldSupportEqualsAndHashCode() {
        Salary salary1 = Salary.of(new BigDecimal("50000"));
        Salary salary2 = Salary.of(new BigDecimal("50000"));
        Salary salary3 = Salary.of(new BigDecimal("60000"));
        
        assertThat(salary1).isEqualTo(salary2);
        assertThat(salary1).isNotEqualTo(salary3);
        assertThat(salary1.hashCode()).isEqualTo(salary2.hashCode());
    }

    @Test
    @DisplayName("Should handle decimal precision")
    void shouldHandleDecimalPrecision() {
        Salary salary1 = Salary.of(new BigDecimal("50000.00"));
        Salary salary2 = Salary.of(new BigDecimal("50000"));
        
        assertThat(salary1.getValue()).isEqualByComparingTo(salary2.getValue());
    }
}