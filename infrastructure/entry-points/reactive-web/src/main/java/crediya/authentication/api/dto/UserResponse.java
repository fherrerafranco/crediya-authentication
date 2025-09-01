package crediya.authentication.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "User information response")
public class UserResponse {
    
    @Schema(description = "User's unique identifier", example = "123456789")
    private String id;
    
    @Schema(description = "User's first name", example = "John")
    private String firstName;
    
    @Schema(description = "User's last name", example = "Doe")
    private String lastName;
    
    @Schema(description = "User's email address", example = "john.doe@example.com")
    private String email;
    
    @Schema(description = "User's identity document number", example = "123456789")
    private String identityDocument;
    
    @Schema(description = "User's phone number", example = "321654987")
    private String phone;
    
    @Schema(description = "User's role identifier", example = "1")
    private String roleId;
    
    @Schema(description = "User's base salary", example = "1000000")
    private BigDecimal baseSalary;
    
    @Schema(description = "User's birth date in YYYY-MM-DD format", example = "1990-01-01")
    private String birthDate;
    
    @Schema(description = "User's address", example = "Street 123 #45-67")
    private String address;
}