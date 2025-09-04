package crediya.authentication.r2dbc;

import crediya.authentication.model.role.Role;
import crediya.authentication.model.role.gateways.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class RoleReactiveRepositoryAdapter implements RoleRepository {
    
    private final RoleReactiveRepository roleReactiveRepository;

    @Override
    public Mono<Role> findById(Integer roleId) {
        return roleReactiveRepository.findById(roleId)
                .map(entity -> Role.builder()
                        .id(entity.getRoleId())
                        .name(entity.getName())
                        .description(entity.getDescription())
                        .build());
    }

    @Override
    public Mono<Boolean> existsById(Integer roleId) {
        return roleReactiveRepository.existsById(roleId);
    }
}