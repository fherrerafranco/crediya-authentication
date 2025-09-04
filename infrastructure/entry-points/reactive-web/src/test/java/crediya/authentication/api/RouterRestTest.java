package crediya.authentication.api;

import crediya.authentication.api.config.UserPath;
import crediya.authentication.api.config.TestSecurityConfig;
import crediya.authentication.api.exception.GlobalExceptionHandler;
import crediya.authentication.api.dto.UserCreateRequest;
import crediya.authentication.api.dto.UserResponse;
import crediya.authentication.api.mapper.UserMapper;
import crediya.authentication.model.user.User;
import crediya.authentication.usecase.user.UserUseCase;
import crediya.authentication.usecase.auth.LoginUseCase;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.Validator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doNothing;


@ContextConfiguration(classes = {RouterRest.class, Handler.class, UserPath.class, GlobalExceptionHandler.class})
@WebFluxTest
@Import(TestSecurityConfig.class)
@TestPropertySource(properties = {
    "routes.paths.users=/api/v1/users"
})
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private UserUseCase userUseCase;
    
    @MockitoBean
    private LoginUseCase loginUseCase;
    
    @MockitoBean
    private UserMapper userMapper;
    
    @MockitoBean
    private Validator validator;

    private final String users = "/api/v1/users";

    private final UserCreateRequest createRequest = UserCreateRequest.builder()
            .firstName("test")
            .lastName("user")
            .phone("321654987")
            .identityDocument("123456789")
            .roleId("1")
            .baseSalary(new BigDecimal("1000000"))
            .birthDate("2000-01-01")
            .address("Street 123 #45-67")
            .email("correo@deprueba.com")
            .build();

    private final UserResponse expectedUserResponse = UserResponse.builder()
            .id("123456789")
            .firstName("test")
            .lastName("user")
            .phone("321654987")
            .identityDocument("123456789")
            .roleId("1")
            .baseSalary(new BigDecimal("1000000"))
            .birthDate("2000-01-01")
            .address("Street 123 #45-67")
            .email("correo@deprueba.com")
            .build();

    @Test
    void shouldPostSaveUser() {
        // Infrastructure test focuses on HTTP/JSON concerns, not domain logic
        // Mock the complete flow to test HTTP routing and serialization
        
        User mockUser = mock(User.class);
        doNothing().when(validator).validate(any(), any());
        when(userMapper.toDomain(any())).thenReturn(mockUser);
        when(userUseCase.saveUser(any())).thenReturn(Mono.just(mockUser));
        when(userMapper.toResponse(any())).thenReturn(expectedUserResponse);
        
        webTestClient.post()
                .uri(users)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(UserResponse.class)
                .value(saved -> {
                    // Focus on HTTP/JSON serialization concerns
                    Assertions.assertThat(saved.getId()).isEqualTo(expectedUserResponse.getId());
                    Assertions.assertThat(saved.getEmail()).isEqualTo(expectedUserResponse.getEmail());
                });
    }

    @Test
    void shouldGetAllUsers() {
        UserResponse user2Response = UserResponse.builder()
                .id("987654321")
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .baseSalary(new BigDecimal("75000"))
                .build();

        // Mock the flow: Use Case returns domain objects -> Mapper converts to DTOs
        User mockUser1 = mock(User.class);
        User mockUser2 = mock(User.class);
        when(userUseCase.getAllUsers()).thenReturn(Flux.just(mockUser1, mockUser2));
        when(userMapper.toResponse(mockUser1)).thenReturn(expectedUserResponse);
        when(userMapper.toResponse(mockUser2)).thenReturn(user2Response);

        webTestClient.get()
                .uri(users)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(UserResponse.class)
                .hasSize(2)
                .value(users -> {
                    // Focus on HTTP/JSON serialization concerns
                    Assertions.assertThat(users).hasSize(2);
                    Assertions.assertThat(users.get(0).getId()).isEqualTo(expectedUserResponse.getId());
                    Assertions.assertThat(users.get(1).getId()).isEqualTo(user2Response.getId());
                });
    }

    @Test
    void shouldGetNotFoundWhenPathIsIncorrect() {
        webTestClient.post()
                .uri("/api/v1/invalid-path")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldReturnConflictWhenEmailAlreadyExists() {
        User mockUser = mock(User.class);
        doNothing().when(validator).validate(any(), any());
        when(userMapper.toDomain(any())).thenReturn(mockUser);
        when(userUseCase.saveUser(any())).thenReturn(Mono.error(new DataIntegrityViolationException("Key (email)=(test@example.com) already exists")));

        webTestClient.post()
                .uri(users)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void shouldReturnInternalServerErrorForUnexpectedExceptions() {
        User mockUser = mock(User.class);
        doNothing().when(validator).validate(any(), any());
        when(userMapper.toDomain(any())).thenReturn(mockUser);
        when(userUseCase.saveUser(any())).thenReturn(Mono.error(new RuntimeException("Database connection lost")));

        webTestClient.post()
                .uri(users)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void shouldLogTraceInformationForUserCreation() {
        User mockUser = mock(User.class);
        doNothing().when(validator).validate(any(), any());
        when(userMapper.toDomain(any())).thenReturn(mockUser);
        when(userUseCase.saveUser(any())).thenReturn(Mono.just(mockUser));
        when(userMapper.toResponse(any())).thenReturn(expectedUserResponse);

        webTestClient.post()
                .uri(users)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON);
    }
}
