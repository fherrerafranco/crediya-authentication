package crediya.authentication.model.auth;

import crediya.authentication.model.role.Role;
import crediya.authentication.model.role.gateways.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {

    @Mock
    private RoleRepository roleRepository;

    private AuthorizationService authorizationService;

    @BeforeEach
    void setUp() {
        authorizationService = new AuthorizationService(roleRepository);
    }

    @Test
    @DisplayName("Should allow ADMIN to create user")
    void shouldAllowAdminToCreateUser() {
        Role adminRole = Role.builder().id(1).name("ADMIN").description("Administrator").build();
        when(roleRepository.findById(1)).thenReturn(Mono.just(adminRole));

        Mono<Boolean> result = authorizationService.canCreateUser(1);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(roleRepository).findById(1);
    }

    @Test
    @DisplayName("Should allow ADVISOR to create user")
    void shouldAllowAdvisorToCreateUser() {
        Role advisorRole = Role.builder().id(2).name("ADVISOR").description("Financial Advisor").build();
        when(roleRepository.findById(2)).thenReturn(Mono.just(advisorRole));

        Mono<Boolean> result = authorizationService.canCreateUser(2);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(roleRepository).findById(2);
    }

    @Test
    @DisplayName("Should not allow CUSTOMER to create user")
    void shouldNotAllowCustomerToCreateUser() {
        Role customerRole = Role.builder().id(3).name("CUSTOMER").description("Customer").build();
        when(roleRepository.findById(3)).thenReturn(Mono.just(customerRole));

        Mono<Boolean> result = authorizationService.canCreateUser(3);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(roleRepository).findById(3);
    }

    @Test
    @DisplayName("Should return false when role not found for create user")
    void shouldReturnFalseWhenRoleNotFoundForCreateUser() {
        when(roleRepository.findById(99)).thenReturn(Mono.empty());

        Mono<Boolean> result = authorizationService.canCreateUser(99);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(roleRepository).findById(99);
    }

    @Test
    @DisplayName("Should allow ADMIN to view all users")
    void shouldAllowAdminToViewAllUsers() {
        Role adminRole = Role.builder().id(1).name("ADMIN").description("Administrator").build();
        when(roleRepository.findById(1)).thenReturn(Mono.just(adminRole));

        Mono<Boolean> result = authorizationService.canViewAllUsers(1);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(roleRepository).findById(1);
    }

    @Test
    @DisplayName("Should allow ADVISOR to view all users")
    void shouldAllowAdvisorToViewAllUsers() {
        Role advisorRole = Role.builder().id(2).name("ADVISOR").description("Financial Advisor").build();
        when(roleRepository.findById(2)).thenReturn(Mono.just(advisorRole));

        Mono<Boolean> result = authorizationService.canViewAllUsers(2);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(roleRepository).findById(2);
    }

    @Test
    @DisplayName("Should not allow CUSTOMER to view all users")
    void shouldNotAllowCustomerToViewAllUsers() {
        Role customerRole = Role.builder().id(3).name("CUSTOMER").description("Customer").build();
        when(roleRepository.findById(3)).thenReturn(Mono.just(customerRole));

        Mono<Boolean> result = authorizationService.canViewAllUsers(3);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(roleRepository).findById(3);
    }

    @Test
    @DisplayName("Should return false when role not found for view all users")
    void shouldReturnFalseWhenRoleNotFoundForViewAllUsers() {
        when(roleRepository.findById(99)).thenReturn(Mono.empty());

        Mono<Boolean> result = authorizationService.canViewAllUsers(99);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(roleRepository).findById(99);
    }

    @Test
    @DisplayName("Should allow CUSTOMER to create their own loan application")
    void shouldAllowCustomerToCreateTheirOwnLoanApplication() {
        Role customerRole = Role.builder().id(3).name("CUSTOMER").description("Customer").build();
        when(roleRepository.findById(3)).thenReturn(Mono.just(customerRole));

        Mono<Boolean> result = authorizationService.canCreateLoanApplication(3, "user123", "user123");

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(roleRepository).findById(3);
    }

    @Test
    @DisplayName("Should not allow CUSTOMER to create loan application for other user")
    void shouldNotAllowCustomerToCreateLoanApplicationForOtherUser() {
        Role customerRole = Role.builder().id(3).name("CUSTOMER").description("Customer").build();
        when(roleRepository.findById(3)).thenReturn(Mono.just(customerRole));

        Mono<Boolean> result = authorizationService.canCreateLoanApplication(3, "user123", "user456");

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(roleRepository).findById(3);
    }

    @Test
    @DisplayName("Should not allow ADMIN to create loan application")
    void shouldNotAllowAdminToCreateLoanApplication() {
        Role adminRole = Role.builder().id(1).name("ADMIN").description("Administrator").build();
        when(roleRepository.findById(1)).thenReturn(Mono.just(adminRole));

        Mono<Boolean> result = authorizationService.canCreateLoanApplication(1, "user123", "user123");

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(roleRepository).findById(1);
    }

    @Test
    @DisplayName("Should not allow ADVISOR to create loan application")
    void shouldNotAllowAdvisorToCreateLoanApplication() {
        Role advisorRole = Role.builder().id(2).name("ADVISOR").description("Financial Advisor").build();
        when(roleRepository.findById(2)).thenReturn(Mono.just(advisorRole));

        Mono<Boolean> result = authorizationService.canCreateLoanApplication(2, "user123", "user123");

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(roleRepository).findById(2);
    }

    @Test
    @DisplayName("Should return false when role not found for loan application")
    void shouldReturnFalseWhenRoleNotFoundForLoanApplication() {
        when(roleRepository.findById(99)).thenReturn(Mono.empty());

        Mono<Boolean> result = authorizationService.canCreateLoanApplication(99, "user123", "user123");

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(roleRepository).findById(99);
    }

    @Test
    @DisplayName("Should handle repository error gracefully")
    void shouldHandleRepositoryErrorGracefully() {
        RuntimeException expectedException = new RuntimeException("Database error");
        when(roleRepository.findById(1)).thenReturn(Mono.error(expectedException));

        Mono<Boolean> result = authorizationService.canCreateUser(1);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(roleRepository).findById(1);
    }

    @Test
    @DisplayName("Should handle null role ID gracefully")
    void shouldHandleNullRoleIdGracefully() {
        when(roleRepository.findById(null)).thenReturn(Mono.empty());

        Mono<Boolean> result = authorizationService.canCreateUser(null);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(roleRepository).findById(null);
    }

}