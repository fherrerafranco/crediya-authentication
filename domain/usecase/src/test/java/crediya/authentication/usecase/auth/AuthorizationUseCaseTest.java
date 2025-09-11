package crediya.authentication.usecase.auth;

import crediya.authentication.model.auth.AuthorizationContext;
import crediya.authentication.model.auth.AuthorizationResult;
import crediya.authentication.model.auth.Permission;
import crediya.authentication.model.role.Role;
import crediya.authentication.model.role.RoleType;
import crediya.authentication.model.role.gateways.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationUseCaseTest {

    @Mock
    private RoleRepository roleRepository;

    private AuthorizationUseCase authorizationUseCase;

    @BeforeEach
    void setUp() {
        authorizationUseCase = new AuthorizationUseCase(roleRepository);
    }

    @Test
    void shouldAuthorizeAdminForCreateUser() {
        // Given
        Role adminRole = Role.builder()
                .id(1)
                .name("ADMIN")
                .description("Admin role")
                .build();
        when(roleRepository.findById(1)).thenReturn(Mono.just(adminRole));

        // When/Then
        StepVerifier.create(authorizationUseCase.authorize("admin-user", 1, Permission.CREATE_USER))
                .assertNext(result -> {
                    assertTrue(result.isAuthorized());
                    assertEquals(Permission.CREATE_USER, result.getPermission());
                    assertNull(result.getReason());
                })
                .verifyComplete();
    }

    @Test
    void shouldDenyCustomerForCreateUser() {
        // Given
        Role customerRole = Role.builder()
                .id(3)
                .name("CUSTOMER")
                .description("Customer role")
                .build();
        when(roleRepository.findById(3)).thenReturn(Mono.just(customerRole));

        // When/Then
        StepVerifier.create(authorizationUseCase.authorize("customer-user", 3, Permission.CREATE_USER))
                .assertNext(result -> {
                    assertFalse(result.isAuthorized());
                    assertEquals(Permission.CREATE_USER, result.getPermission());
                    assertNotNull(result.getReason());
                })
                .verifyComplete();
    }

    @Test
    void shouldDenyWhenUserIdIsNull() {
        // When/Then
        StepVerifier.create(authorizationUseCase.authorize(null, 1, Permission.CREATE_USER))
                .assertNext(result -> {
                    assertFalse(result.isAuthorized());
                    assertTrue(result.getReason().contains("Invalid authorization context"));
                })
                .verifyComplete();
    }

    @Test
    void shouldDenyWhenRoleIdIsNull() {
        // When/Then
        StepVerifier.create(authorizationUseCase.authorize("user-123", null, Permission.CREATE_USER))
                .assertNext(result -> {
                    assertFalse(result.isAuthorized());
                    assertTrue(result.getReason().contains("Invalid authorization context"));
                })
                .verifyComplete();
    }

    @Test
    void shouldDenyWhenPermissionIsNull() {
        // When/Then
        StepVerifier.create(authorizationUseCase.authorize("user-123", 1, null))
                .assertNext(result -> {
                    assertFalse(result.isAuthorized());
                    assertTrue(result.getReason().contains("Invalid authorization context"));
                })
                .verifyComplete();
    }

    @Test
    void shouldDenyWhenRoleNotFound() {
        // Given
        when(roleRepository.findById(999)).thenReturn(Mono.empty());

        // When/Then
        StepVerifier.create(authorizationUseCase.authorize("user-123", 999, Permission.CREATE_USER))
                .assertNext(result -> {
                    assertFalse(result.isAuthorized());
                    assertTrue(result.getReason().contains("Invalid role ID: 999"));
                })
                .verifyComplete();
    }

    @Test
    void shouldAuthorizeDirectlyWithContext() {
        // Given
        AuthorizationContext context = AuthorizationContext.builder()
                .userId("admin-user")
                .roleType(RoleType.ADMIN)
                .build();

        // When/Then
        StepVerifier.create(authorizationUseCase.authorize(context, Permission.VIEW_ALL_USERS))
                .assertNext(result -> {
                    assertTrue(result.isAuthorized());
                    assertEquals(Permission.VIEW_ALL_USERS, result.getPermission());
                })
                .verifyComplete();
    }

    @Test
    void shouldDenyWithNullContext() {
        // When/Then
        StepVerifier.create(authorizationUseCase.authorize(null, Permission.CREATE_USER))
                .assertNext(result -> {
                    assertFalse(result.isAuthorized());
                    assertTrue(result.getReason().contains("Invalid authorization context"));
                })
                .verifyComplete();
    }

    @Test
    void shouldDenyCustomerCreatingLoanForOthers() {
        // Given
        AuthorizationContext context = AuthorizationContext.builder()
                .userId("customer-123")
                .roleType(RoleType.CUSTOMER)
                .targetResourceId("other-user")
                .build();

        // When/Then
        StepVerifier.create(authorizationUseCase.authorize(context, Permission.CREATE_LOAN_APPLICATION))
                .assertNext(result -> {
                    assertFalse(result.isAuthorized());
                    assertTrue(result.getReason().contains("Customers can only create loan applications for themselves"));
                })
                .verifyComplete();
    }

    @Test
    void shouldAllowCustomerCreatingLoanForSelf() {
        // Given
        String userId = "customer-123";
        AuthorizationContext context = AuthorizationContext.builder()
                .userId(userId)
                .roleType(RoleType.CUSTOMER)
                .targetResourceId(userId)
                .build();

        // When/Then
        StepVerifier.create(authorizationUseCase.authorize(context, Permission.CREATE_LOAN_APPLICATION))
                .assertNext(result -> {
                    assertTrue(result.isAuthorized());
                    assertEquals(Permission.CREATE_LOAN_APPLICATION, result.getPermission());
                })
                .verifyComplete();
    }

    @Test
    void shouldDenyCustomerViewingOthersLoanApplication() {
        // Given
        AuthorizationContext context = AuthorizationContext.builder()
                .userId("customer-123")
                .roleType(RoleType.CUSTOMER)
                .targetResourceId("other-user")
                .build();

        // When/Then
        StepVerifier.create(authorizationUseCase.authorize(context, Permission.VIEW_OWN_LOAN_APPLICATION))
                .assertNext(result -> {
                    assertFalse(result.isAuthorized());
                    assertTrue(result.getReason().contains("Users can only view their own loan applications"));
                })
                .verifyComplete();
    }

    @Test
    void shouldAllowAdminViewingAllLoanApplications() {
        // Given
        AuthorizationContext context = AuthorizationContext.builder()
                .userId("admin-123")
                .roleType(RoleType.ADMIN)
                .targetResourceId("any-user")
                .build();

        // When/Then
        StepVerifier.create(authorizationUseCase.authorize(context, Permission.VIEW_ALL_LOAN_APPLICATIONS))
                .assertNext(result -> {
                    assertTrue(result.isAuthorized());
                    assertEquals(Permission.VIEW_ALL_LOAN_APPLICATIONS, result.getPermission());
                })
                .verifyComplete();
    }

    @Test
    void shouldDenyCustomerForUpdateUserPermission() {
        // Given - Customer role doesn't have UPDATE_USER permission at all
        AuthorizationContext context = AuthorizationContext.builder()
                .userId("customer-123")
                .roleType(RoleType.CUSTOMER)
                .targetResourceId("any-user")
                .build();

        // When/Then
        StepVerifier.create(authorizationUseCase.authorize(context, Permission.UPDATE_USER))
                .assertNext(result -> {
                    assertFalse(result.isAuthorized());
                    assertTrue(result.getReason().contains("CUSTOMER") && result.getReason().contains("UPDATE_USER"));
                })
                .verifyComplete();
    }

    @Test
    void shouldAllowAdvisorUpdatingUsers() {
        // Given - Advisor has UPDATE_USER permission
        AuthorizationContext context = AuthorizationContext.builder()
                .userId("advisor-123")
                .roleType(RoleType.ADVISOR)
                .targetResourceId("some-user")
                .build();

        // When/Then
        StepVerifier.create(authorizationUseCase.authorize(context, Permission.UPDATE_USER))
                .assertNext(result -> {
                    assertTrue(result.isAuthorized());
                    assertEquals(Permission.UPDATE_USER, result.getPermission());
                })
                .verifyComplete();
    }

    @Test
    void shouldWorkWithConvenienceMethods() {
        // Given
        Role adminRole = Role.builder().id(1).name("ADMIN").build();
        when(roleRepository.findById(1)).thenReturn(Mono.just(adminRole));

        // When/Then
        StepVerifier.create(authorizationUseCase.canCreateUser("admin", 1))
                .assertNext(result -> assertTrue(result))
                .verifyComplete();

        StepVerifier.create(authorizationUseCase.canViewAllUsers("admin", 1))
                .assertNext(result -> assertTrue(result))
                .verifyComplete();
    }

    @Test
    void shouldHandleInvalidRoleName() {
        // Given
        Role invalidRole = Role.builder()
                .id(999)
                .name("INVALID_ROLE")
                .description("Invalid role")
                .build();
        when(roleRepository.findById(999)).thenReturn(Mono.just(invalidRole));

        // When/Then
        StepVerifier.create(authorizationUseCase.authorize("user-123", 999, Permission.CREATE_USER))
                .assertNext(result -> {
                    assertFalse(result.isAuthorized());
                    assertTrue(result.getReason().contains("Invalid role name: INVALID_ROLE"));
                })
                .verifyComplete();
    }

    @Test
    void shouldDenyWhenContextHasNullRoleType() {
        // Given
        AuthorizationContext context = AuthorizationContext.builder()
                .userId("user-123")
                .roleType(null)
                .build();

        // When/Then
        StepVerifier.create(authorizationUseCase.authorize(context, Permission.CREATE_USER))
                .assertNext(result -> {
                    assertFalse(result.isAuthorized());
                    assertTrue(result.getReason().contains("Invalid authorization context"));
                })
                .verifyComplete();
    }

    @Test
    void shouldAllowCustomerCreatingLoanWithNullTargetResourceId() {
        // Given - Customer creating loan application with null target resource (for themselves)
        AuthorizationContext context = AuthorizationContext.builder()
                .userId("customer-123")
                .roleType(RoleType.CUSTOMER)
                .targetResourceId(null)
                .build();

        // When/Then
        StepVerifier.create(authorizationUseCase.authorize(context, Permission.CREATE_LOAN_APPLICATION))
                .assertNext(result -> {
                    assertTrue(result.isAuthorized());
                    assertEquals(Permission.CREATE_LOAN_APPLICATION, result.getPermission());
                })
                .verifyComplete();
    }

    @Test
    void shouldAllowViewOwnLoanApplicationWithNullTargetResourceId() {
        // Given - Customer viewing own loan application with null target resource
        AuthorizationContext context = AuthorizationContext.builder()
                .userId("customer-123")
                .roleType(RoleType.CUSTOMER)
                .targetResourceId(null)
                .build();

        // When/Then
        StepVerifier.create(authorizationUseCase.authorize(context, Permission.VIEW_OWN_LOAN_APPLICATION))
                .assertNext(result -> {
                    assertTrue(result.isAuthorized());
                    assertEquals(Permission.VIEW_OWN_LOAN_APPLICATION, result.getPermission());
                })
                .verifyComplete();
    }

    @Test
    void shouldAllowCustomerViewingOwnLoanApplication() {
        // Given
        String customerId = "customer-123";
        AuthorizationContext context = AuthorizationContext.builder()
                .userId(customerId)
                .roleType(RoleType.CUSTOMER)
                .targetResourceId(customerId)
                .build();

        // When/Then
        StepVerifier.create(authorizationUseCase.authorize(context, Permission.VIEW_OWN_LOAN_APPLICATION))
                .assertNext(result -> {
                    assertTrue(result.isAuthorized());
                    assertEquals(Permission.VIEW_OWN_LOAN_APPLICATION, result.getPermission());
                })
                .verifyComplete();
    }

    @Test
    void shouldDenyCustomerViewingOthersLoanApplicationWithCorrectMessage() {
        // Given
        AuthorizationContext context = AuthorizationContext.builder()
                .userId("customer-123")
                .roleType(RoleType.CUSTOMER)
                .targetResourceId("other-customer")
                .build();

        // When/Then
        StepVerifier.create(authorizationUseCase.authorize(context, Permission.VIEW_OWN_LOAN_APPLICATION))
                .assertNext(result -> {
                    assertFalse(result.isAuthorized());
                    assertTrue(result.getReason().contains("Users can only view their own loan applications"));
                })
                .verifyComplete();
    }

    @Test
    void shouldTestConvenienceMethodsForLoanApplications() {
        // Given
        Role customerRole = Role.builder().id(3).name("CUSTOMER").build();
        when(roleRepository.findById(3)).thenReturn(Mono.just(customerRole));

        String customerId = "customer-123";

        // When/Then - Customer creating loan application for self
        StepVerifier.create(authorizationUseCase.canCreateLoanApplication(customerId, 3, customerId))
                .assertNext(result -> assertTrue(result))
                .verifyComplete();

        // Customer viewing own loan application
        StepVerifier.create(authorizationUseCase.canViewLoanApplication(customerId, 3, customerId))
                .assertNext(result -> assertTrue(result))
                .verifyComplete();

        // Customer trying to view other's loan application
        StepVerifier.create(authorizationUseCase.canViewLoanApplication(customerId, 3, "other-customer"))
                .assertNext(result -> assertFalse(result))
                .verifyComplete();
    }

    @Test
    void shouldTestConvenienceMethodsForUserManagement() {
        // Given
        Role adminRole = Role.builder().id(1).name("ADMIN").build();
        Role customerRole = Role.builder().id(3).name("CUSTOMER").build();
        when(roleRepository.findById(1)).thenReturn(Mono.just(adminRole));
        when(roleRepository.findById(3)).thenReturn(Mono.just(customerRole));

        // When/Then - Admin can update any user
        StepVerifier.create(authorizationUseCase.canUpdateUser("admin-123", 1, "any-user"))
                .assertNext(result -> assertTrue(result))
                .verifyComplete();

        // Admin can delete any user
        StepVerifier.create(authorizationUseCase.canDeleteUser("admin-123", 1, "any-user"))
                .assertNext(result -> assertTrue(result))
                .verifyComplete();

        // Customer cannot update users (no permission)
        StepVerifier.create(authorizationUseCase.canUpdateUser("customer-123", 3, "any-user"))
                .assertNext(result -> assertFalse(result))
                .verifyComplete();

        // Customer cannot delete users (no permission)
        StepVerifier.create(authorizationUseCase.canDeleteUser("customer-123", 3, "any-user"))
                .assertNext(result -> assertFalse(result))
                .verifyComplete();
    }
}