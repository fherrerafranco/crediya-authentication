package crediya.authentication.api.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private final String token;
    private final String tokenType;
    private final Long expiresIn;
}