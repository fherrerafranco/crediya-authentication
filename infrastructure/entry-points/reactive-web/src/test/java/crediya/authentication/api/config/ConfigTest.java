package crediya.authentication.api.config;

import crediya.authentication.api.Handler;
import crediya.authentication.api.RouterRest;
import crediya.authentication.api.config.UserPath;
import crediya.authentication.api.config.AuthorizationService;
import crediya.authentication.api.config.TestSecurityConfig;
import crediya.authentication.api.dto.UserResponse;
import crediya.authentication.api.mapper.UserMapper;
import crediya.authentication.usecase.user.UserUseCase;
import crediya.authentication.usecase.auth.LoginUseCase;
import crediya.authentication.model.auth.gateways.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, UserPath.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class, TestSecurityConfig.class})
@TestPropertySource(properties = {
    "routes.paths.users=/api/v1/users",
    "cors.allowed-origins=*",
    "spring.security.user.name=test",
    "spring.security.user.password=test"
})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserUseCase userUseCase;
    
    @MockitoBean
    private LoginUseCase loginUseCase;
    
    @MockitoBean
    private UserMapper userMapper;
    
    @MockitoBean
    private AuthorizationService authorizationService;
    
    @MockitoBean
    private PasswordEncoder passwordEncoder;

    private final UserResponse userResponseOne = UserResponse.builder()
            .id("123456789")
            .firstName("John")
            .lastName("Doe")
            .phone("321654987")
            .identityDocument("123456789")
            .roleId("1")
            .baseSalary(new BigDecimal("1000000"))
            .birthDate("2000-01-01")
            .address("Street 123 #45-67")
            .email("john.doe@example.com")
            .build();

    private final UserResponse userResponseTwo = UserResponse.builder()
            .id("987654321")
            .firstName("Jane")
            .lastName("Smith")
            .phone("123987654")
            .identityDocument("987654321")
            .roleId("2")
            .baseSalary(new BigDecimal("75000"))
            .birthDate("1985-05-15")
            .address("456 Oak Ave")
            .email("jane.smith@example.com")
            .build();

    @BeforeEach
    void setUp() {
        // Mock the use case to return any domain objects (we don't care about structure)
        when(userUseCase.getAllUsers()).thenReturn(Flux.empty());
        // Mock the mapper to return the DTOs we want to test
        when(userMapper.toResponse(any())).thenReturn(userResponseOne, userResponseTwo);
        // Mock authorization service to allow access
        when(authorizationService.hasAdminOrAdvisorRole(any())).thenReturn(true);
    }

    @Test
    void corsConfigurationShouldAllowOrigins() {
        webTestClient.get()
                .uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }
}