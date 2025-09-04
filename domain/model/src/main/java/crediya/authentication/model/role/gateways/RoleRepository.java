package crediya.authentication.model.role.gateways;

import crediya.authentication.model.role.Role;
import reactor.core.publisher.Mono;

public interface RoleRepository {
    
    Mono<Role> findById(Integer roleId);
    
    Mono<Boolean> existsById(Integer roleId);
}