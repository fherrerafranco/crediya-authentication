package crediya.authentication.usecase.auth;

import crediya.authentication.model.auth.AuthenticationResult;
import crediya.authentication.model.auth.LoginCredentials;
import crediya.authentication.model.auth.gateways.JwtTokenManager;
import crediya.authentication.model.auth.gateways.PasswordEncoder;
import crediya.authentication.model.exception.BusinessRuleViolationException;
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
                .filter(user -> user.getPasswordHash() != null && 
                               passwordEncoder.matches(credentials.getPassword(), user.getPasswordHash()))
                .switchIfEmpty(Mono.error(new BusinessRuleViolationException("Invalid email or password")))
                .map(user -> {
                    String token = jwtTokenManager.generateToken(user.getId(), user.getRoleId());
                    return AuthenticationResult.builder()
                            .token(token)
                            .userId(user.getId())
                            .roleId(user.getRoleId())
                            .expiresAt(LocalDateTime.now().plusHours(24))
                            .build();
                });
    }
}