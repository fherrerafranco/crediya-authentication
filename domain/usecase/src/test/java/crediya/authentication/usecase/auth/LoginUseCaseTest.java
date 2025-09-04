package crediya.authentication.usecase.auth;

import crediya.authentication.model.auth.AuthenticationResult;
import crediya.authentication.model.auth.LoginCredentials;
import crediya.authentication.model.auth.gateways.JwtTokenManager;
import crediya.authentication.model.auth.gateways.PasswordEncoder;
import crediya.authentication.model.exception.BusinessRuleViolationException;
import crediya.authentication.model.user.User;
import crediya.authentication.model.user.gateways.UserRepository;
import crediya.authentication.model.valueobjects.Email;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;
    
    @Mock
    private JwtTokenManager jwtTokenManager;

    private LoginUseCase loginUseCase;

    @BeforeEach
    void setUp() {
        loginUseCase = new LoginUseCase(userRepository, passwordEncoder, jwtTokenManager);
    }

    @Test
    @DisplayName("Should authenticate user successfully with valid credentials")
    void shouldAuthenticateUserSuccessfullyWithValidCredentials() {
        LoginCredentials credentials = new LoginCredentials(
                Email.of("john.doe@example.com"),
                "correctPassword"
        );

        User user = User.builder()
                .id("user123")
                .email(Email.of("john.doe@example.com"))
                .passwordHash("$2a$12$hashedPassword")
                .roleId(1)
                .build();

        when(userRepository.findByEmail(credentials.getEmail())).thenReturn(Mono.just(user));
        when(passwordEncoder.matches("correctPassword", "$2a$12$hashedPassword")).thenReturn(true);
        when(jwtTokenManager.generateToken("user123", 1)).thenReturn("jwt-token-123");

        Mono<AuthenticationResult> result = loginUseCase.authenticate(credentials);

        StepVerifier.create(result)
                .assertNext(authResult -> {
                    assertThat(authResult.getToken()).isEqualTo("jwt-token-123");
                    assertThat(authResult.getUserId()).isEqualTo("user123");
                    assertThat(authResult.getRoleId()).isEqualTo(1);
                    assertThat(authResult.getExpiresAt()).isNotNull();
                })
                .verifyComplete();

        verify(userRepository).findByEmail(credentials.getEmail());
        verify(passwordEncoder).matches("correctPassword", "$2a$12$hashedPassword");
        verify(jwtTokenManager).generateToken("user123", 1);
    }

    @Test
    @DisplayName("Should fail authentication when user not found")
    void shouldFailAuthenticationWhenUserNotFound() {
        LoginCredentials credentials = new LoginCredentials(
                Email.of("nonexistent@example.com"),
                "anyPassword"
        );

        when(userRepository.findByEmail(credentials.getEmail())).thenReturn(Mono.empty());

        Mono<AuthenticationResult> result = loginUseCase.authenticate(credentials);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof BusinessRuleViolationException &&
                    throwable.getMessage().equals("Invalid email or password"))
                .verify();

        verify(userRepository).findByEmail(credentials.getEmail());
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtTokenManager, never()).generateToken(any(), any());
    }

    @Test
    @DisplayName("Should fail authentication when password is incorrect")
    void shouldFailAuthenticationWhenPasswordIsIncorrect() {
        LoginCredentials credentials = new LoginCredentials(
                Email.of("john.doe@example.com"),
                "wrongPassword"
        );

        User user = User.builder()
                .id("user123")
                .email(Email.of("john.doe@example.com"))
                .passwordHash("$2a$12$hashedPassword")
                .roleId(1)
                .build();

        when(userRepository.findByEmail(credentials.getEmail())).thenReturn(Mono.just(user));
        when(passwordEncoder.matches("wrongPassword", "$2a$12$hashedPassword")).thenReturn(false);

        Mono<AuthenticationResult> result = loginUseCase.authenticate(credentials);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof BusinessRuleViolationException &&
                    throwable.getMessage().equals("Invalid email or password"))
                .verify();

        verify(userRepository).findByEmail(credentials.getEmail());
        verify(passwordEncoder).matches("wrongPassword", "$2a$12$hashedPassword");
        verify(jwtTokenManager, never()).generateToken(any(), any());
    }

    @Test
    @DisplayName("Should fail authentication when user has no password hash")
    void shouldFailAuthenticationWhenUserHasNoPasswordHash() {
        LoginCredentials credentials = new LoginCredentials(
                Email.of("john.doe@example.com"),
                "anyPassword"
        );

        User user = User.builder()
                .id("user123")
                .email(Email.of("john.doe@example.com"))
                .passwordHash(null)
                .roleId(1)
                .build();

        when(userRepository.findByEmail(credentials.getEmail())).thenReturn(Mono.just(user));

        Mono<AuthenticationResult> result = loginUseCase.authenticate(credentials);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof BusinessRuleViolationException &&
                    throwable.getMessage().equals("Invalid email or password"))
                .verify();

        verify(userRepository).findByEmail(credentials.getEmail());
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtTokenManager, never()).generateToken(any(), any());
    }

    @Test
    @DisplayName("Should fail authentication when user has empty password hash")
    void shouldFailAuthenticationWhenUserHasEmptyPasswordHash() {
        LoginCredentials credentials = new LoginCredentials(
                Email.of("john.doe@example.com"),
                "anyPassword"
        );

        User user = User.builder()
                .id("user123")
                .email(Email.of("john.doe@example.com"))
                .passwordHash("")
                .roleId(1)
                .build();

        when(userRepository.findByEmail(credentials.getEmail())).thenReturn(Mono.just(user));
        when(passwordEncoder.matches("anyPassword", "")).thenReturn(false);

        Mono<AuthenticationResult> result = loginUseCase.authenticate(credentials);

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> 
                    throwable instanceof BusinessRuleViolationException &&
                    throwable.getMessage().equals("Invalid email or password"))
                .verify();

        verify(userRepository).findByEmail(credentials.getEmail());
        verify(passwordEncoder).matches("anyPassword", "");
        verify(jwtTokenManager, never()).generateToken(any(), any());
    }

    @Test
    @DisplayName("Should handle repository error gracefully")
    void shouldHandleRepositoryErrorGracefully() {
        LoginCredentials credentials = new LoginCredentials(
                Email.of("john.doe@example.com"),
                "anyPassword"
        );

        RuntimeException repositoryException = new RuntimeException("Database connection error");
        when(userRepository.findByEmail(credentials.getEmail())).thenReturn(Mono.error(repositoryException));

        Mono<AuthenticationResult> result = loginUseCase.authenticate(credentials);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(userRepository).findByEmail(credentials.getEmail());
        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtTokenManager, never()).generateToken(any(), any());
    }

    @Test
    @DisplayName("Should handle password encoder error gracefully")
    void shouldHandlePasswordEncoderErrorGracefully() {
        LoginCredentials credentials = new LoginCredentials(
                Email.of("john.doe@example.com"),
                "testPassword"
        );

        User user = User.builder()
                .id("user123")
                .email(Email.of("john.doe@example.com"))
                .passwordHash("$2a$12$hashedPassword")
                .roleId(1)
                .build();

        RuntimeException encoderException = new RuntimeException("Password encoding error");
        when(userRepository.findByEmail(credentials.getEmail())).thenReturn(Mono.just(user));
        when(passwordEncoder.matches("testPassword", "$2a$12$hashedPassword")).thenThrow(encoderException);

        Mono<AuthenticationResult> result = loginUseCase.authenticate(credentials);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(userRepository).findByEmail(credentials.getEmail());
        verify(passwordEncoder).matches("testPassword", "$2a$12$hashedPassword");
        verify(jwtTokenManager, never()).generateToken(any(), any());
    }

    @Test
    @DisplayName("Should handle JWT token manager error gracefully")
    void shouldHandleJwtTokenManagerErrorGracefully() {
        LoginCredentials credentials = new LoginCredentials(
                Email.of("john.doe@example.com"),
                "correctPassword"
        );

        User user = User.builder()
                .id("user123")
                .email(Email.of("john.doe@example.com"))
                .passwordHash("$2a$12$hashedPassword")
                .roleId(1)
                .build();

        RuntimeException jwtException = new RuntimeException("JWT generation error");
        when(userRepository.findByEmail(credentials.getEmail())).thenReturn(Mono.just(user));
        when(passwordEncoder.matches("correctPassword", "$2a$12$hashedPassword")).thenReturn(true);
        when(jwtTokenManager.generateToken("user123", 1)).thenThrow(jwtException);

        Mono<AuthenticationResult> result = loginUseCase.authenticate(credentials);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(userRepository).findByEmail(credentials.getEmail());
        verify(passwordEncoder).matches("correctPassword", "$2a$12$hashedPassword");
        verify(jwtTokenManager).generateToken("user123", 1);
    }

    @Test
    @DisplayName("Should authenticate user with different roles")
    void shouldAuthenticateUserWithDifferentRoles() {
        // Test with ADMIN role
        testSuccessfulAuthenticationWithRole(1, "admin@example.com", "adminToken");
        
        // Test with ADVISOR role
        testSuccessfulAuthenticationWithRole(2, "advisor@example.com", "advisorToken");
        
        // Test with CUSTOMER role
        testSuccessfulAuthenticationWithRole(3, "customer@example.com", "customerToken");
    }

    private void testSuccessfulAuthenticationWithRole(Integer roleId, String email, String expectedToken) {
        LoginCredentials credentials = new LoginCredentials(
                Email.of(email),
                "password"
        );

        User user = User.builder()
                .id("user-" + roleId)
                .email(Email.of(email))
                .passwordHash("$2a$12$hash")
                .roleId(roleId)
                .build();

        when(userRepository.findByEmail(credentials.getEmail())).thenReturn(Mono.just(user));
        when(passwordEncoder.matches("password", "$2a$12$hash")).thenReturn(true);
        when(jwtTokenManager.generateToken("user-" + roleId, roleId)).thenReturn(expectedToken);

        StepVerifier.create(loginUseCase.authenticate(credentials))
                .assertNext(authResult -> {
                    assertThat(authResult.getToken()).isEqualTo(expectedToken);
                    assertThat(authResult.getUserId()).isEqualTo("user-" + roleId);
                    assertThat(authResult.getRoleId()).isEqualTo(roleId);
                })
                .verifyComplete();
    }

}