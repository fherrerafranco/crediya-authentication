package crediya.authentication.api.mapper;

import crediya.authentication.api.dto.UserCreateRequest;
import crediya.authentication.api.dto.UserResponse;
import crediya.authentication.model.user.User;
import crediya.authentication.model.valueobjects.Email;
import crediya.authentication.model.valueobjects.Salary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "email", target = "email", qualifiedByName = "stringToEmail")
    @Mapping(source = "baseSalary", target = "baseSalary", qualifiedByName = "bigDecimalToSalary")
    @Mapping(source = "password", target = "passwordHash")
    User toDomain(UserCreateRequest request);
    
    @Mapping(source = "email", target = "email", qualifiedByName = "emailToString")
    @Mapping(source = "baseSalary", target = "baseSalary", qualifiedByName = "salaryToBigDecimal")
    UserResponse toResponse(User user);
    
    @Named("stringToEmail")
    default Email stringToEmail(String email) {
        return email != null ? Email.of(email) : null;
    }
    
    @Named("emailToString")
    default String emailToString(Email email) {
        return email != null ? email.getValue() : null;
    }
    
    @Named("bigDecimalToSalary")
    default Salary bigDecimalToSalary(BigDecimal amount) {
        return amount != null ? Salary.of(amount) : null;
    }
    
    @Named("salaryToBigDecimal")
    default BigDecimal salaryToBigDecimal(Salary salary) {
        return salary != null ? salary.getValue() : null;
    }
}