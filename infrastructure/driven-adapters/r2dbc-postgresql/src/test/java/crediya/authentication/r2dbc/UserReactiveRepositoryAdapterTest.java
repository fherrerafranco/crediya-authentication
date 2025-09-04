package crediya.authentication.r2dbc;

import crediya.authentication.model.user.User;
import crediya.authentication.model.valueobjects.Email;
import crediya.authentication.model.valueobjects.Salary;
import crediya.authentication.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.math.BigDecimal;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {

    UserReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    UserReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    @Mock
    TransactionalOperator transactionalOperator;

    @BeforeEach
    void setup() {
        repositoryAdapter = new UserReactiveRepositoryAdapter(repository, mapper, transactionalOperator);
    }

    private final UUID testUuid1 = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private final UUID testUuid2 = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    
    private final UserEntity userEntity = UserEntity.builder()
            .id(testUuid1)
            .firstName("test")
            .lastName("user")
            .phone("321654987")
            .identityDocument("123456789")
            .roleId(1)
            .baseSalary(new BigDecimal("1000000"))
            .birthDate("2000-01-01")
            .address("Street 123 #45-67")
            .email("correo@deprueba.com")
            .build();

    private final User user = User.builder()
            .id(testUuid1.toString())
            .firstName("test")
            .lastName("user")
            .phone("321654987")
            .identityDocument("123456789")
            .roleId(1)
            .baseSalary(Salary.of(new BigDecimal("1000000")))
            .birthDate("2000-01-01")
            .address("Street 123 #45-67")
            .email(Email.of("correo@deprueba.com"))
            .build();

    @Test
    void shouldSaveUser() {
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.just(userEntity));
        when(transactionalOperator.transactional(any(Mono.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Mono<User> result = repositoryAdapter.save(user);

        StepVerifier.create(result)
                .expectNextMatches(savedUser -> 
                    savedUser.getId().equals(user.getId()) &&
                    savedUser.getFirstName().equals(user.getFirstName()) &&
                    savedUser.getLastName().equals(user.getLastName()) &&
                    savedUser.getEmail().equals(user.getEmail()) &&
                    savedUser.getBaseSalary().equals(user.getBaseSalary()) &&
                    savedUser.getIdentityDocument().equals(user.getIdentityDocument()) &&
                    savedUser.getPhone().equals(user.getPhone()) &&
                    savedUser.getRoleId().equals(user.getRoleId()) &&
                    savedUser.getBirthDate().equals(user.getBirthDate()) &&
                    savedUser.getAddress().equals(user.getAddress())
                )
                .verifyComplete();
    }

    @Test
    void shouldGetAllUsers() {
        UserEntity userEntity2 = UserEntity.builder()
                .id(testUuid2)
                .firstName("Jane")
                .lastName("Doe")
                .phone("0987654321")
                .identityDocument("987654321")
                .roleId(2)
                .baseSalary(new BigDecimal("75000"))
                .birthDate("1985-05-15")
                .address("456 Oak Ave")
                .email("jane.doe@example.com")
                .build();

        User user2 = User.builder()
                .id(testUuid2.toString())
                .firstName("Jane")
                .lastName("Doe")
                .phone("0987654321")
                .identityDocument("987654321")
                .roleId(2)
                .baseSalary(Salary.of(new BigDecimal("75000")))
                .birthDate("1985-05-15")
                .address("456 Oak Ave")
                .email(Email.of("jane.doe@example.com"))
                .build();

        when(repository.findAll()).thenReturn(Flux.just(userEntity, userEntity2));

        Flux<User> result = repositoryAdapter.getAll();

        StepVerifier.create(result)
                .expectNextMatches(savedUser -> 
                    savedUser.getId().equals(user.getId()) &&
                    savedUser.getFirstName().equals(user.getFirstName()) &&
                    savedUser.getLastName().equals(user.getLastName()) &&
                    savedUser.getEmail().equals(user.getEmail()) &&
                    savedUser.getBaseSalary().equals(user.getBaseSalary())
                )
                .expectNextMatches(savedUser -> 
                    savedUser.getId().equals(user2.getId()) &&
                    savedUser.getFirstName().equals(user2.getFirstName()) &&
                    savedUser.getLastName().equals(user2.getLastName()) &&
                    savedUser.getEmail().equals(user2.getEmail()) &&
                    savedUser.getBaseSalary().equals(user2.getBaseSalary())
                )
                .verifyComplete();
    }
}
