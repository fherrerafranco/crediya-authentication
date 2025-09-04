package crediya.authentication.r2dbc;

import crediya.authentication.r2dbc.entity.RoleEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface RoleReactiveRepository extends ReactiveCrudRepository<RoleEntity, Integer> {
}