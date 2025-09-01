package crediya.authentication.model.user.gateways;

import crediya.authentication.model.user.User;
import crediya.authentication.model.valueobjects.Email;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserRepository {

    Mono<User> save(User user);

    Flux<User> getAll();
    
    Mono<Boolean> existsByEmail(Email email);

}
