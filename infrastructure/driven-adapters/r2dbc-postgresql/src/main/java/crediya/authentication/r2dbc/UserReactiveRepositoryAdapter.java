package crediya.authentication.r2dbc;

import crediya.authentication.model.user.User;
import crediya.authentication.model.valueobjects.Email;
import crediya.authentication.model.valueobjects.Salary;
import crediya.authentication.model.user.gateways.UserRepository;
import crediya.authentication.r2dbc.entity.UserEntity;
import crediya.authentication.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.UUID;

@Slf4j
@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        User,
        UserEntity,
        UUID,
        UserReactiveRepository
>implements UserRepository {
    
    private final UserReactiveRepository userReactiveRepository;
    private final TransactionalOperator transactionalOperator;
    
    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, UserReactiveRepositoryAdapter::entityToDomain);
        this.userReactiveRepository = repository;
        this.transactionalOperator = transactionalOperator;
    }
    
    private static User entityToDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return User.builder()
                .id(entity.getId() != null ? entity.getId().toString() : null)
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail() != null ? Email.of(entity.getEmail()) : null)
                .identityDocument(entity.getIdentityDocument())
                .phone(entity.getPhone())
                .roleId(entity.getRoleId())
                .baseSalary(entity.getBaseSalary() != null ? Salary.of(entity.getBaseSalary()) : null)
                .birthDate(entity.getBirthDate())
                .address(entity.getAddress())
                .build();
    }
    
    @Override
    protected UserEntity toData(User user) {
        if (user == null) {
            return null;
        }
        
        return UserEntity.builder()
                .id(user.getId() != null && !user.getId().trim().isEmpty() ? UUID.fromString(user.getId()) : null)
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail() != null ? user.getEmail().getValue() : null)
                .identityDocument(user.getIdentityDocument())
                .phone(user.getPhone())
                .roleId(user.getRoleId())
                .baseSalary(user.getBaseSalary() != null ? user.getBaseSalary().getValue() : null)
                .birthDate(user.getBirthDate())
                .address(user.getAddress())
                .build();
    }

    @Override
    public Mono<User> save(User user) {
        if (user == null) {
            return Mono.error(new IllegalArgumentException("User cannot be null"));
        }
        
        log.info("Saving user with email: {}", user.getEmail());
        
        // R2DBC will automatically generate UUID for new entities (when ID is null)
        UserEntity userEntity = toData(user);
        return userReactiveRepository.save(userEntity)
                .map(this::toEntity)
                .doOnSuccess(savedUser -> log.info("Successfully saved user with id: {}", savedUser.getId()))
                .doOnError(error -> log.error("Database save operation failed for user with email: {}, error: {}", 
                        user.getEmail(), error.getMessage()))
                .as(transactionalOperator::transactional);
    }

    @Override
    public Flux<User> getAll() {
        log.info("Retrieving all users from database");
        return super.findAll()
                .doOnError(error -> log.error("Database query failed for getAllUsers: {}", error.getMessage()));
    }

    @Override
    public Mono<Boolean> existsByEmail(Email email) {
        return userReactiveRepository.existsByEmail(email.getValue())
                .doOnError(error -> log.error("Error checking email existence: {}", error.getMessage()));
    }

}
