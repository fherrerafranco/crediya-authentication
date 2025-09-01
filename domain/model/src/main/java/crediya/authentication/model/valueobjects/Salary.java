package crediya.authentication.model.valueobjects;

import crediya.authentication.model.constants.DomainErrorMessages;
import crediya.authentication.model.exception.ValidationException;
import java.math.BigDecimal;
import java.util.Objects;

public class Salary {
    private static final BigDecimal MIN_SALARY = BigDecimal.ZERO;
    private static final BigDecimal MAX_SALARY = new BigDecimal("15000000");
    
    private final BigDecimal value;
    
    private Salary(BigDecimal value) {
        this.value = value;
    }
    
    public static Salary of(BigDecimal amount) {
        if (amount == null) {
            throw new ValidationException("baseSalary", DomainErrorMessages.SALARY_NULL);
        }
        
        if (amount.compareTo(MIN_SALARY) < 0) {
            throw new ValidationException("baseSalary", DomainErrorMessages.SALARY_BELOW_MINIMUM);
        }
        
        if (amount.compareTo(MAX_SALARY) > 0) {
            throw new ValidationException("baseSalary", DomainErrorMessages.SALARY_ABOVE_MAXIMUM);
        }
        
        return new Salary(amount);
    }
    
    public BigDecimal getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Salary salary = (Salary) o;
        return Objects.equals(value, salary.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
}