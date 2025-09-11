package crediya.authentication.r2dbc.mapper;

import crediya.authentication.model.user.User;
import crediya.authentication.model.valueobjects.Email;
import crediya.authentication.model.valueobjects.Salary;
import crediya.authentication.r2dbc.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UserEntityMapper {
    
    UserEntityMapper INSTANCE = Mappers.getMapper(UserEntityMapper.class);
    
    @Mapping(source = "id", target = "id", qualifiedByName = "uuidToString")
    @Mapping(source = "email", target = "email", qualifiedByName = "stringToEmail")
    @Mapping(source = "baseSalary", target = "baseSalary", qualifiedByName = "bigDecimalToSalary")
    User entityToDomain(UserEntity entity);
    
    @Mapping(source = "id", target = "id", qualifiedByName = "stringToUuid")
    @Mapping(source = "email", target = "email", qualifiedByName = "emailToString")
    @Mapping(source = "baseSalary", target = "baseSalary", qualifiedByName = "salaryToBigDecimal")
    UserEntity domainToEntity(User user);
    
    @Named("uuidToString")
    default String uuidToString(UUID uuid) {
        return uuid != null ? uuid.toString() : null;
    }
    
    @Named("stringToUuid")
    default UUID stringToUuid(String id) {
        return (id != null && !id.trim().isEmpty()) ? UUID.fromString(id) : null;
    }
    
    @Named("stringToEmail")
    default Email stringToEmail(String email) {
        return email != null ? Email.of(email) : null;
    }
    
    @Named("emailToString")
    default String emailToString(Email email) {
        return email != null ? email.getValue() : null;
    }
    
    @Named("bigDecimalToSalary")
    default Salary bigDecimalToSalary(BigDecimal baseSalary) {
        return baseSalary != null ? Salary.of(baseSalary) : null;
    }
    
    @Named("salaryToBigDecimal")
    default BigDecimal salaryToBigDecimal(Salary salary) {
        return salary != null ? salary.getValue() : null;
    }
}