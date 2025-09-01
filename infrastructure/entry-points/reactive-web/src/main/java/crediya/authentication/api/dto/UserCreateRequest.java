package crediya.authentication.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for creating a new user")
public class UserCreateRequest {
    
    @NotBlank(message = "First name is required")
    @Schema(description = "User's first name", example = "John", required = true)
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Schema(description = "User's last name", example = "Doe", required = true)
    private String lastName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(description = "User's email address", example = "john.doe@example.com", required = true)
    private String email;
    
    @Schema(description = "User's identity document number", example = "123456789")
    private String identityDocument;
    
    @Schema(description = "User's phone number", example = "321654987")
    private String phone;
    
    @Schema(description = "User's role identifier", example = "1")
    private String roleId;
    
    @NotNull(message = "Base salary is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Base salary must be at least 0")
    @DecimalMax(value = "15000000.0", inclusive = true, message = "Base salary must not exceed 15,000,000")
    @Schema(description = "User's base salary (between 0 and 15,000,000)", example = "1000000", required = true)
    private BigDecimal baseSalary;
    
    @Schema(description = "User's birth date in YYYY-MM-DD format", example = "1990-01-01")
    private String birthDate;
    
    @Schema(description = "User's address", example = "Street 123 #45-67")
    private String address;
}