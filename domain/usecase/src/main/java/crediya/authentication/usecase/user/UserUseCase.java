package crediya.authentication.usecase.user;

import crediya.authentication.model.constants.DomainErrorMessages;
import crediya.authentication.model.exception.BusinessRuleViolationException;
import crediya.authentication.model.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import crediya.authentication.model.user.User;
import crediya.authentication.model.valueobjects.Email;
import crediya.authentication.model.user.gateways.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;

    public Mono<User> saveUser(User user){
        if (user == null) {
            return Mono.error(new ValidationException(DomainErrorMessages.USER_NULL));
        }
        
        return checkEmailUniqueness(user.getEmail())
                .flatMap(isUnique -> {
                    if (!isUnique) {
                        return Mono.error(new BusinessRuleViolationException(String.format(DomainErrorMessages.EMAIL_ALREADY_REGISTERED, user.getEmail().getValue())));
                    }
                    return userRepository.save(user);
                });
    }

    public Flux<User> getAllUsers(){
        return userRepository.getAll();
    }

    private Mono<Boolean> checkEmailUniqueness(Email email) {
        return userRepository.existsByEmail(email)
                .map(exists -> !exists)
                .defaultIfEmpty(true);
    }
}
