package crediya.authentication.usecase.user;

import crediya.authentication.model.constants.DomainErrorMessages;
import crediya.authentication.model.exception.BusinessRuleViolationException;
import crediya.authentication.model.exception.ValidationException;
import crediya.authentication.model.auth.gateways.PasswordEncoder;
import crediya.authentication.model.role.gateways.RoleRepository;
import lombok.RequiredArgsConstructor;
import crediya.authentication.model.user.User;
import crediya.authentication.model.valueobjects.Email;
import crediya.authentication.model.user.gateways.UserRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class UserUseCase {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public Mono<User> saveUser(User user){
        if (user == null) {
            return Mono.error(new ValidationException(DomainErrorMessages.USER_NULL));
        }
        
        return roleRepository.existsById(user.getRoleId())
                .filter(exists -> exists)
                .switchIfEmpty(Mono.error(new BusinessRuleViolationException("Invalid role ID")))
                .then(checkEmailUniqueness(user.getEmail()))
                .flatMap(isUnique -> {
                    if (!isUnique) {
                        return Mono.error(new BusinessRuleViolationException(String.format(DomainErrorMessages.EMAIL_ALREADY_REGISTERED, user.getEmail().getValue())));
                    }
                    // Hash password if provided
                    User userToSave = user;
                    if (user.getPasswordHash() != null && !user.getPasswordHash().trim().isEmpty()) {
                        String hashedPassword = passwordEncoder.encode(user.getPasswordHash());
                        userToSave = user.toBuilder().passwordHash(hashedPassword).build();
                    }
                    return userRepository.save(userToSave);
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
