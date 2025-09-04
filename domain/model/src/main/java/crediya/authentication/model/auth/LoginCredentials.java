package crediya.authentication.model.auth;

import crediya.authentication.model.valueobjects.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginCredentials {
    private final Email email;
    private final String password;
}