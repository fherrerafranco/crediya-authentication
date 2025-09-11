package crediya.authentication.api.dto;

import crediya.authentication.api.constants.ErrorMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = ErrorMessages.EMAIL_REQUIRED)
    @Email(message = ErrorMessages.EMAIL_MUST_BE_VALID)
    private String email;
    
    @NotBlank(message = ErrorMessages.PASSWORD_REQUIRED)
    private String password;
}