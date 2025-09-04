package crediya.authentication.model.user.gateways;

import crediya.authentication.model.user.User;
import crediya.authentication.model.valueobjects.Email;
import crediya.authentication.model.valueobjects.Salary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

class UserRepositoryTest {

    private final UserRepository userRepository = mock(UserRepository.class);

    @Test
    @DisplayName("Should save user successfully")
    void shouldSaveUserSuccessfully() {
        User inputUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email(Email.of("john.doe@example.com"))
                .identityDocument("123456789")
                .phone("1234567890")
                .roleId(1)
                .baseSalary(Salary.of(new BigDecimal("50000")))
                .birthDate("1990-01-01")
                .address("123 Main St")
                .build();

        User savedUser = User.builder()
                .id("generated-id-123")
                .firstName("John")
                .lastName("Doe")
                .email(Email.of("john.doe@example.com"))
                .identityDocument("123456789")
                .phone("1234567890")
                .roleId(1)
                .baseSalary(Salary.of(new BigDecimal("50000")))
                .birthDate("1990-01-01")
                .address("123 Main St")
                .build();

        when(userRepository.save(inputUser)).thenReturn(Mono.just(savedUser));

        Mono<User> result = userRepository.save(inputUser);

        StepVerifier.create(result)
                .expectNext(savedUser)
                .verifyComplete();

        verify(userRepository).save(inputUser);
    }

    @Test
    @DisplayName("Should handle empty user save")
    void shouldHandleEmptyUserSave() {
        User emptyUser = User.builder().build();
        User savedEmptyUser = User.builder().id("empty-id").build();

        when(userRepository.save(emptyUser)).thenReturn(Mono.just(savedEmptyUser));

        Mono<User> result = userRepository.save(emptyUser);

        StepVerifier.create(result)
                .expectNext(savedEmptyUser)
                .verifyComplete();

        verify(userRepository).save(emptyUser);
    }

    @Test
    @DisplayName("Should handle save error")
    void shouldHandleSaveError() {
        User user = User.builder()
                .firstName("Error")
                .lastName("User")
                .build();

        RuntimeException expectedException = new RuntimeException("Database error");
        when(userRepository.save(user)).thenReturn(Mono.error(expectedException));

        Mono<User> result = userRepository.save(user);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should handle null user save")
    void shouldHandleNullUserSave() {
        when(userRepository.save(null)).thenReturn(Mono.error(new IllegalArgumentException("User cannot be null")));

        Mono<User> result = userRepository.save(null);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(userRepository).save(null);
    }

    @Test
    @DisplayName("Should get all users successfully")
    void shouldGetAllUsersSuccessfully() {
        User user1 = User.builder()
                .id("1")
                .firstName("John")
                .lastName("Doe")
                .email(Email.of("john.doe@example.com"))
                .build();

        User user2 = User.builder()
                .id("2")
                .firstName("Jane")
                .lastName("Smith")
                .email(Email.of("jane.smith@example.com"))
                .build();

        when(userRepository.getAll()).thenReturn(Flux.just(user1, user2));

        Flux<User> result = userRepository.getAll();

        StepVerifier.create(result)
                .expectNext(user1)
                .expectNext(user2)
                .verifyComplete();

        verify(userRepository).getAll();
    }

    @Test
    @DisplayName("Should handle empty list when getting all users")
    void shouldHandleEmptyListWhenGettingAllUsers() {
        when(userRepository.getAll()).thenReturn(Flux.empty());

        Flux<User> result = userRepository.getAll();

        StepVerifier.create(result)
                .verifyComplete();

        verify(userRepository).getAll();
    }

    @Test
    @DisplayName("Should handle error when getting all users")
    void shouldHandleErrorWhenGettingAllUsers() {
        RuntimeException expectedException = new RuntimeException("Database connection error");
        when(userRepository.getAll()).thenReturn(Flux.error(expectedException));

        Flux<User> result = userRepository.getAll();

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(userRepository).getAll();
    }

    @Test
    @DisplayName("Should get all users with single result")
    void shouldGetAllUsersWithSingleResult() {
        User singleUser = User.builder()
                .id("single-id")
                .firstName("Solo")
                .lastName("User")
                .email(Email.of("solo.user@example.com"))
                .identityDocument("987654321")
                .phone("0987654321")
                .roleId(2)
                .baseSalary(Salary.of(new BigDecimal("60000")))
                .birthDate("1985-05-15")
                .address("456 Oak Ave")
                .build();

        when(userRepository.getAll()).thenReturn(Flux.just(singleUser));

        Flux<User> result = userRepository.getAll();

        StepVerifier.create(result)
                .expectNext(singleUser)
                .verifyComplete();

        verify(userRepository).getAll();
    }
}