package crediya.authentication.usecase.user;

import crediya.authentication.model.user.User;
import crediya.authentication.model.valueobjects.Email;
import crediya.authentication.model.valueobjects.Salary;
import crediya.authentication.model.user.gateways.UserRepository;
import crediya.authentication.model.role.gateways.RoleRepository;
import crediya.authentication.model.auth.gateways.PasswordEncoder;
import crediya.authentication.model.exception.ValidationException;
import crediya.authentication.model.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private RoleRepository roleRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;

    private UserUseCase userUseCase;

    @BeforeEach
    void setUp() {
        userUseCase = new UserUseCase(userRepository, roleRepository, passwordEncoder);
    }
    
    private void mockValidRole(Integer roleId) {
        when(roleRepository.existsById(roleId)).thenReturn(Mono.just(true));
    }

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

        when(roleRepository.existsById(1)).thenReturn(Mono.just(true));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(false));
        when(userRepository.save(inputUser)).thenReturn(Mono.just(savedUser));

        Mono<User> result = userUseCase.saveUser(inputUser);

        StepVerifier.create(result)
                .expectNext(savedUser)
                .verifyComplete();

        verify(userRepository, times(1)).existsByEmail(any(Email.class));
        verify(userRepository, times(1)).save(inputUser);
    }

    @Test
    @DisplayName("Should handle repository error")
    void shouldHandleRepositoryError() {
        User user = User.builder()
                .firstName("Error")
                .lastName("User")
                .email(Email.of("error@example.com"))
                .roleId(1)
                .baseSalary(Salary.of(new BigDecimal("50000")))
                .build();

        when(roleRepository.existsById(1)).thenReturn(Mono.just(true));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(false));

        RuntimeException expectedException = new RuntimeException("Repository error");
        when(userRepository.save(user)).thenReturn(Mono.error(expectedException));

        Mono<User> result = userUseCase.saveUser(user);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should save user successfully even with missing first name")
    void shouldSaveUserSuccessfullyEvenWithMissingFirstName() {
        User user = User.builder()
                .lastName("Doe")
                .email(Email.of("john.doe@example.com"))
                .roleId(1)
                .baseSalary(Salary.of(new BigDecimal("50000")))
                .build();

        User savedUser = User.builder()
                .id("generated-id")
                .lastName("Doe")
                .email(Email.of("john.doe@example.com"))
                .roleId(1)
                .baseSalary(Salary.of(new BigDecimal("50000")))
                .build();

        when(roleRepository.existsById(1)).thenReturn(Mono.just(true));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(false));
        when(userRepository.save(user)).thenReturn(Mono.just(savedUser));

        Mono<User> result = userUseCase.saveUser(user);

        StepVerifier.create(result)
                .expectNext(savedUser)
                .verifyComplete();

        verify(userRepository, times(1)).existsByEmail(any(Email.class));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should save user successfully even with missing last name")
    void shouldSaveUserSuccessfullyEvenWithMissingLastName() {
        User user = User.builder()
                .firstName("John")
                .email(Email.of("john.doe@example.com"))
                .roleId(1)
                .baseSalary(Salary.of(new BigDecimal("50000")))
                .build();

        User savedUser = User.builder()
                .id("generated-id")
                .firstName("John")
                .email(Email.of("john.doe@example.com"))
                .roleId(1)
                .baseSalary(Salary.of(new BigDecimal("50000")))
                .build();

        when(roleRepository.existsById(1)).thenReturn(Mono.just(true));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(false));
        when(userRepository.save(user)).thenReturn(Mono.just(savedUser));

        Mono<User> result = userUseCase.saveUser(user);

        StepVerifier.create(result)
                .expectNext(savedUser)
                .verifyComplete();

        verify(userRepository, times(1)).existsByEmail(any(Email.class));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should fail validation when email is invalid")
    void shouldFailValidationWhenEmailIsInvalid() {
        // Email validation happens at value object creation time
        StepVerifier.create(Mono.fromCallable(() -> Email.of("invalid-email")))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    @DisplayName("Should fail validation when base salary is negative")
    void shouldFailValidationWhenBaseSalaryIsNegative() {
        // Salary validation happens at value object creation time
        StepVerifier.create(Mono.fromCallable(() -> Salary.of(new BigDecimal("-1000"))))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    @DisplayName("Should fail validation when base salary exceeds maximum")
    void shouldFailValidationWhenBaseSalaryExceedsMaximum() {
        // Salary validation happens at value object creation time
        StepVerifier.create(Mono.fromCallable(() -> Salary.of(new BigDecimal("16000000"))))
                .expectError(ValidationException.class)
                .verify();
    }

    @Test
    @DisplayName("Should handle null user")
    void shouldHandleNullUser() {
        Mono<User> result = userUseCase.saveUser(null);

        StepVerifier.create(result)
                .expectError(ValidationException.class)
                .verify();

        verify(userRepository, never()).save(any());
        verify(userRepository, never()).existsByEmail(any());
    }

    @Test
    @DisplayName("Should fail when email already exists")
    void shouldFailWhenEmailAlreadyExists() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email(Email.of("existing@example.com"))
                .roleId(1)
                .baseSalary(Salary.of(new BigDecimal("50000")))
                .build();

        when(roleRepository.existsById(1)).thenReturn(Mono.just(true));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(true));

        Mono<User> result = userUseCase.saveUser(user);

        StepVerifier.create(result)
                .expectError(BusinessRuleViolationException.class)
                .verify();

        verify(userRepository, times(1)).existsByEmail(any(Email.class));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should save user with complete data")
    void shouldSaveUserWithCompleteData() {
        User completeUser = User.builder()
                .firstName("Alice")
                .lastName("Johnson")
                .email(Email.of("alice.johnson@example.com"))
                .identityDocument("987654321")
                .phone("0987654321")
                .roleId(2)
                .baseSalary(Salary.of(new BigDecimal("75000")))
                .birthDate("1985-03-15")
                .address("789 Pine St")
                .build();

        User savedCompleteUser = User.builder()
                .id("complete-id-789")
                .firstName("Alice")
                .lastName("Johnson")
                .email(Email.of("alice.johnson@example.com"))
                .identityDocument("987654321")
                .phone("0987654321")
                .roleId(2)
                .baseSalary(Salary.of(new BigDecimal("75000")))
                .birthDate("1985-03-15")
                .address("789 Pine St")
                .build();

        when(roleRepository.existsById(2)).thenReturn(Mono.just(true));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(false));
        when(userRepository.save(completeUser)).thenReturn(Mono.just(savedCompleteUser));

        Mono<User> result = userUseCase.saveUser(completeUser);

        StepVerifier.create(result)
                .expectNext(savedCompleteUser)
                .verifyComplete();

        verify(userRepository, times(1)).save(completeUser);
    }

    @Test
    @DisplayName("Should handle repository timeout")
    void shouldHandleRepositoryTimeout() {
        User user = User.builder()
                .firstName("Timeout")
                .lastName("Test")
                .email(Email.of("timeout@example.com"))
                .roleId(1)
                .baseSalary(Salary.of(new BigDecimal("50000")))
                .build();

        when(roleRepository.existsById(1)).thenReturn(Mono.just(true));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(false));
        when(userRepository.save(any(User.class))).thenReturn(Mono.never());

        Mono<User> result = userUseCase.saveUser(user);

        StepVerifier.create(result)
                .expectTimeout(java.time.Duration.ofSeconds(1))
                .verify();

        verify(userRepository, times(1)).save(user);
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

        Flux<User> result = userUseCase.getAllUsers();

        StepVerifier.create(result)
                .expectNext(user1)
                .expectNext(user2)
                .verifyComplete();

        verify(userRepository, times(1)).getAll();
    }

    @Test
    @DisplayName("Should handle empty list when getting all users")
    void shouldHandleEmptyListWhenGettingAllUsers() {
        when(userRepository.getAll()).thenReturn(Flux.empty());

        Flux<User> result = userUseCase.getAllUsers();

        StepVerifier.create(result)
                .verifyComplete();

        verify(userRepository, times(1)).getAll();
    }

    @Test
    @DisplayName("Should handle repository error when getting all users")
    void shouldHandleRepositoryErrorWhenGettingAllUsers() {
        RuntimeException expectedException = new RuntimeException("Repository error");
        when(userRepository.getAll()).thenReturn(Flux.error(expectedException));

        Flux<User> result = userUseCase.getAllUsers();

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(userRepository, times(1)).getAll();
    }

    @Test
    @DisplayName("Should handle checkEmailUniqueness when repository returns empty")
    void shouldHandleCheckEmailUniquenessWhenRepositoryReturnsEmpty() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email(Email.of("john.doe@example.com"))
                .roleId(1)
                .baseSalary(Salary.of(new BigDecimal("50000")))
                .build();

        User savedUser = User.builder()
                .id("generated-id")
                .firstName("John")
                .lastName("Doe")
                .email(Email.of("john.doe@example.com"))
                .roleId(1)
                .baseSalary(Salary.of(new BigDecimal("50000")))
                .build();

        // Repository returns empty, defaultIfEmpty(true) should make it unique
        when(roleRepository.existsById(1)).thenReturn(Mono.just(true));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(Mono.empty());
        when(userRepository.save(user)).thenReturn(Mono.just(savedUser));

        Mono<User> result = userUseCase.saveUser(user);

        StepVerifier.create(result)
                .expectNext(savedUser)
                .verifyComplete();

        verify(userRepository, times(1)).existsByEmail(any(Email.class));
        verify(userRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("Should handle repository error during email existence check")
    void shouldHandleRepositoryErrorDuringEmailExistenceCheck() {
        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email(Email.of("john.doe@example.com"))
                .roleId(1)
                .baseSalary(Salary.of(new BigDecimal("50000")))
                .build();

        when(roleRepository.existsById(1)).thenReturn(Mono.just(true));
        RuntimeException expectedException = new RuntimeException("Database connection error");
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(Mono.error(expectedException));

        Mono<User> result = userUseCase.saveUser(user);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(userRepository, times(1)).existsByEmail(any(Email.class));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should handle user with minimal required data")
    void shouldHandleUserWithMinimalRequiredData() {
        User minimalUser = User.builder()
                .email(Email.of("minimal@example.com"))
                .roleId(1)
                .baseSalary(Salary.of(BigDecimal.ZERO))
                .build();

        User savedMinimalUser = User.builder()
                .id("minimal-id")
                .email(Email.of("minimal@example.com"))
                .roleId(1)
                .baseSalary(Salary.of(BigDecimal.ZERO))
                .build();

        when(roleRepository.existsById(1)).thenReturn(Mono.just(true));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(false));
        when(userRepository.save(minimalUser)).thenReturn(Mono.just(savedMinimalUser));

        Mono<User> result = userUseCase.saveUser(minimalUser);

        StepVerifier.create(result)
                .expectNext(savedMinimalUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle user with maximum salary")
    void shouldHandleUserWithMaximumSalary() {
        User maxSalaryUser = User.builder()
                .firstName("Rich")
                .lastName("Person")
                .email(Email.of("rich@example.com"))
                .roleId(1)
                .baseSalary(Salary.of(new BigDecimal("15000000")))
                .build();

        User savedMaxSalaryUser = User.builder()
                .id("rich-id")
                .firstName("Rich")
                .lastName("Person")
                .email(Email.of("rich@example.com"))
                .roleId(1)
                .baseSalary(Salary.of(new BigDecimal("15000000")))
                .build();

        when(roleRepository.existsById(1)).thenReturn(Mono.just(true));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(false));
        when(userRepository.save(maxSalaryUser)).thenReturn(Mono.just(savedMaxSalaryUser));

        Mono<User> result = userUseCase.saveUser(maxSalaryUser);

        StepVerifier.create(result)
                .expectNext(savedMaxSalaryUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should handle user with all fields populated")
    void shouldHandleUserWithAllFieldsPopulated() {
        User completeUser = User.builder()
                .id("existing-id")
                .firstName("Complete")
                .lastName("User")
                .email(Email.of("complete@example.com"))
                .identityDocument("COMP123456789")
                .phone("+1-555-123-4567")
                .roleId(1)
                .baseSalary(Salary.of(new BigDecimal("85000.50")))
                .birthDate("1990-12-25")
                .address("123 Complete Street, Full City, State 12345")
                .build();

        User savedCompleteUser = User.builder()
                .id("saved-complete-id")
                .firstName("Complete")
                .lastName("User")
                .email(Email.of("complete@example.com"))
                .identityDocument("COMP123456789")
                .phone("+1-555-123-4567")
                .roleId(1)
                .baseSalary(Salary.of(new BigDecimal("85000.50")))
                .birthDate("1990-12-25")
                .address("123 Complete Street, Full City, State 12345")
                .build();

        when(roleRepository.existsById(1)).thenReturn(Mono.just(true));
        when(userRepository.existsByEmail(any(Email.class))).thenReturn(Mono.just(false));
        when(userRepository.save(completeUser)).thenReturn(Mono.just(savedCompleteUser));

        Mono<User> result = userUseCase.saveUser(completeUser);

        StepVerifier.create(result)
                .expectNext(savedCompleteUser)
                .verifyComplete();
    }


    @Test
    @DisplayName("Should validate email format through value object")
    void shouldValidateEmailFormatThroughValueObject() {
        // This test ensures that email validation happens at the value object level
        // before it even reaches the use case
        StepVerifier.create(
            Mono.fromCallable(() -> User.builder()
                .firstName("Invalid")
                .email(Email.of("not-an-email"))
                .baseSalary(Salary.of(new BigDecimal("50000")))
                .build())
        )
        .expectError(ValidationException.class)
        .verify();
    }

    @Test
    @DisplayName("Should validate salary range through value object")
    void shouldValidateSalaryRangeThroughValueObject() {
        // This test ensures that salary validation happens at the value object level
        StepVerifier.create(
            Mono.fromCallable(() -> User.builder()
                .firstName("Invalid")
                .email(Email.of("valid@example.com"))
                .baseSalary(Salary.of(new BigDecimal("-1")))
                .build())
        )
        .expectError(ValidationException.class)
        .verify();
    }
}