package crediya.authentication.model.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class AuthenticationResult {
    private final String token;
    private final String userId;
    private final Integer roleId;
    private final LocalDateTime expiresAt;
}