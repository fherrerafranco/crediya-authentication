package crediya.authentication.model.role.gateways;

import crediya.authentication.model.role.Role;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.*;

class RoleRepositoryTest {

    private final RoleRepository roleRepository = mock(RoleRepository.class);

    @Test
    @DisplayName("Should find role by ID successfully")
    void shouldFindRoleByIdSuccessfully() {
        Role adminRole = Role.builder()
                .id(1)
                .name("ADMIN")
                .description("Administrator role")
                .build();

        when(roleRepository.findById(1)).thenReturn(Mono.just(adminRole));

        Mono<Role> result = roleRepository.findById(1);

        StepVerifier.create(result)
                .expectNext(adminRole)
                .verifyComplete();

        verify(roleRepository).findById(1);
    }

    @Test
    @DisplayName("Should return empty when role not found")
    void shouldReturnEmptyWhenRoleNotFound() {
        when(roleRepository.findById(99)).thenReturn(Mono.empty());

        Mono<Role> result = roleRepository.findById(99);

        StepVerifier.create(result)
                .verifyComplete();

        verify(roleRepository).findById(99);
    }

    @Test
    @DisplayName("Should handle error when finding role by ID")
    void shouldHandleErrorWhenFindingRoleById() {
        RuntimeException expectedException = new RuntimeException("Database error");
        when(roleRepository.findById(1)).thenReturn(Mono.error(expectedException));

        Mono<Role> result = roleRepository.findById(1);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(roleRepository).findById(1);
    }

    @Test
    @DisplayName("Should check role existence successfully")
    void shouldCheckRoleExistenceSuccessfully() {
        when(roleRepository.existsById(1)).thenReturn(Mono.just(true));

        Mono<Boolean> result = roleRepository.existsById(1);

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();

        verify(roleRepository).existsById(1);
    }

    @Test
    @DisplayName("Should return false when role does not exist")
    void shouldReturnFalseWhenRoleDoesNotExist() {
        when(roleRepository.existsById(99)).thenReturn(Mono.just(false));

        Mono<Boolean> result = roleRepository.existsById(99);

        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();

        verify(roleRepository).existsById(99);
    }

    @Test
    @DisplayName("Should handle error when checking role existence")
    void shouldHandleErrorWhenCheckingRoleExistence() {
        RuntimeException expectedException = new RuntimeException("Database connection error");
        when(roleRepository.existsById(1)).thenReturn(Mono.error(expectedException));

        Mono<Boolean> result = roleRepository.existsById(1);

        StepVerifier.create(result)
                .expectError(RuntimeException.class)
                .verify();

        verify(roleRepository).existsById(1);
    }

    @Test
    @DisplayName("Should handle null role ID gracefully")
    void shouldHandleNullRoleIdGracefully() {
        when(roleRepository.findById(null)).thenReturn(Mono.error(new IllegalArgumentException("Role ID cannot be null")));

        Mono<Role> result = roleRepository.findById(null);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(roleRepository).findById(null);
    }

    @Test
    @DisplayName("Should handle null role ID in exists check")
    void shouldHandleNullRoleIdInExistsCheck() {
        when(roleRepository.existsById(null)).thenReturn(Mono.error(new IllegalArgumentException("Role ID cannot be null")));

        Mono<Boolean> result = roleRepository.existsById(null);

        StepVerifier.create(result)
                .expectError(IllegalArgumentException.class)
                .verify();

        verify(roleRepository).existsById(null);
    }

    @Test
    @DisplayName("Should find different roles by different IDs")
    void shouldFindDifferentRolesByDifferentIds() {
        Role adminRole = Role.builder().id(1).name("ADMIN").description("Administrator").build();
        Role advisorRole = Role.builder().id(2).name("ADVISOR").description("Financial Advisor").build();
        Role customerRole = Role.builder().id(3).name("CUSTOMER").description("Customer").build();

        when(roleRepository.findById(1)).thenReturn(Mono.just(adminRole));
        when(roleRepository.findById(2)).thenReturn(Mono.just(advisorRole));
        when(roleRepository.findById(3)).thenReturn(Mono.just(customerRole));

        StepVerifier.create(roleRepository.findById(1))
                .expectNext(adminRole)
                .verifyComplete();

        StepVerifier.create(roleRepository.findById(2))
                .expectNext(advisorRole)
                .verifyComplete();

        StepVerifier.create(roleRepository.findById(3))
                .expectNext(customerRole)
                .verifyComplete();

        verify(roleRepository).findById(1);
        verify(roleRepository).findById(2);
        verify(roleRepository).findById(3);
    }

    @Test
    @DisplayName("Should verify exists check for multiple roles")
    void shouldVerifyExistsCheckForMultipleRoles() {
        when(roleRepository.existsById(1)).thenReturn(Mono.just(true));
        when(roleRepository.existsById(2)).thenReturn(Mono.just(true));
        when(roleRepository.existsById(3)).thenReturn(Mono.just(true));
        when(roleRepository.existsById(99)).thenReturn(Mono.just(false));

        StepVerifier.create(roleRepository.existsById(1)).expectNext(true).verifyComplete();
        StepVerifier.create(roleRepository.existsById(2)).expectNext(true).verifyComplete();
        StepVerifier.create(roleRepository.existsById(3)).expectNext(true).verifyComplete();
        StepVerifier.create(roleRepository.existsById(99)).expectNext(false).verifyComplete();

        verify(roleRepository).existsById(1);
        verify(roleRepository).existsById(2);
        verify(roleRepository).existsById(3);
        verify(roleRepository).existsById(99);
    }
}