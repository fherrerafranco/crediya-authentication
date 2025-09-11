package crediya.authentication.usecase.auth;

import crediya.authentication.model.auth.AuthenticationResult;
import crediya.authentication.model.auth.LoginCredentials;
import crediya.authentication.model.auth.gateways.JwtTokenManager;
import crediya.authentication.model.auth.gateways.PasswordEncoder;
import crediya.authentication.model.exception.BusinessRuleViolationException;
import crediya.authentication.model.role.RoleType;
import crediya.authentication.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class LoginUseCase {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenManager jwtTokenManager;

    public Mono<AuthenticationResult> authenticate(LoginCredentials credentials) {
        return userRepository.findByEmail(credentials.getEmail())
                .switchIfEmpty(Mono.error(new BusinessRuleViolationException("Invalid email or password")))
                .filter(user -> {
                    if (user.getPasswordHash() == null) {
                        return false;
                    }
                    return passwordEncoder.matches(credentials.getPassword(), user.getPasswordHash());
                })
                .switchIfEmpty(Mono.error(new BusinessRuleViolationException("Invalid email or password")))
                .map(user -> {
                    // Get role name from role ID for more secure JWT
                    RoleType roleType = RoleType.fromId(Integer.valueOf(user.getRoleId()));
                    String roleName = roleType.getName();
                    
                    String token = jwtTokenManager.generateToken(user.getId(), roleName);
                    return AuthenticationResult.builder()
                            .token(token)
                            .userId(user.getId())
                            .roleId(user.getRoleId())
                            .expiresAt(LocalDateTime.now().plusHours(24))
                            .build();
                });
    }
}