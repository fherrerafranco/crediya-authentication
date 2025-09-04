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
        System.out.println("DEBUG: Authenticating user with email: " + credentials.getEmail().getValue());
        return userRepository.findByEmail(credentials.getEmail())
                .doOnNext(user -> System.out.println("DEBUG: Found user: " + user.getEmail().getValue() + 
                    ", passwordHash present: " + (user.getPasswordHash() != null)))
                .switchIfEmpty(Mono.error(new BusinessRuleViolationException("Invalid email or password")))
                .filter(user -> {
                    if (user.getPasswordHash() == null) {
                        System.out.println("DEBUG: User " + user.getEmail().getValue() + " has no password hash");
                        return false;
                    }
                    boolean matches = passwordEncoder.matches(credentials.getPassword(), user.getPasswordHash());
                    System.out.println("DEBUG: Password match for user " + user.getEmail().getValue() + ": " + matches);
                    System.out.println("DEBUG: Provided password length: " + credentials.getPassword().length() + 
                        ", Hash: " + user.getPasswordHash());
                    return matches;
                })
                .switchIfEmpty(Mono.error(new BusinessRuleViolationException("Invalid email or password")))
                .map(user -> {
                    System.out.println("DEBUG: Authentication successful for user: " + user.getEmail().getValue());
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