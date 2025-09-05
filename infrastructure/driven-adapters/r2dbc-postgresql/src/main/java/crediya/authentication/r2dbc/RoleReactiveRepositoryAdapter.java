package crediya.authentication.r2dbc;

import crediya.authentication.model.role.Role;
import crediya.authentication.model.role.gateways.RoleRepository;
import crediya.authentication.r2dbc.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class RoleReactiveRepositoryAdapter implements RoleRepository {
    
    private final RoleReactiveRepository roleReactiveRepository;
    private final RoleMapper roleMapper;

    @Override
    public Mono<Role> findById(Integer roleId) {
        return roleReactiveRepository.findById(roleId)
                .map(roleMapper::entityToDomain);
    }

    @Override
    public Mono<Boolean> existsById(Integer roleId) {
        return roleReactiveRepository.existsById(roleId);
    }
}