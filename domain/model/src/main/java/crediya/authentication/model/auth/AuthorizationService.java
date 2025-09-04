package crediya.authentication.model.auth;

import crediya.authentication.model.role.gateways.RoleRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AuthorizationService {
    
    private final RoleRepository roleRepository;
    
    public Mono<Boolean> canCreateUser(Integer roleId) {
        return roleRepository.findById(roleId)
                .map(role -> "ADMIN".equals(role.getName()) || "ADVISOR".equals(role.getName()))
                .defaultIfEmpty(false);
    }
    
    public Mono<Boolean> canViewAllUsers(Integer roleId) {
        return roleRepository.findById(roleId)
                .map(role -> "ADMIN".equals(role.getName()) || "ADVISOR".equals(role.getName()))
                .defaultIfEmpty(false);
    }
    
    public Mono<Boolean> canCreateLoanApplication(Integer roleId, String requestingUserId, String targetUserId) {
        return roleRepository.findById(roleId)
                .map(role -> "CUSTOMER".equals(role.getName()) && requestingUserId.equals(targetUserId))
                .defaultIfEmpty(false);
    }
}