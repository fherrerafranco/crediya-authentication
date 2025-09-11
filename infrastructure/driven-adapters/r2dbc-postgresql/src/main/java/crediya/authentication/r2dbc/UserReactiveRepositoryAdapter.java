package crediya.authentication.r2dbc;

import crediya.authentication.model.user.User;
import crediya.authentication.model.valueobjects.Email;
import crediya.authentication.model.user.gateways.UserRepository;
import crediya.authentication.r2dbc.entity.UserEntity;
import crediya.authentication.r2dbc.helper.ReactiveAdapterOperations;
import crediya.authentication.r2dbc.mapper.UserMapper;
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
    private final UserMapper userMapper;
    
    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper, 
                                        TransactionalOperator transactionalOperator, UserMapper userMapper) {
        super(repository, mapper, userMapper::entityToDomain);
        this.userReactiveRepository = repository;
        this.transactionalOperator = transactionalOperator;
        this.userMapper = userMapper;
    }
    
    
    @Override
    protected UserEntity toData(User user) {
        return userMapper.domainToEntity(user);
    }

    @Override
    public Mono<User> save(User user) {
        if (user == null) {
            return Mono.error(new IllegalArgumentException("User cannot be null"));
        }
        log.info("Saving user with email: {}", user.getEmail());
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

    @Override
    public Mono<User> findByEmail(Email email) {
        return userReactiveRepository.findByEmail(email.getValue())
                .map(userMapper::entityToDomain)
                .doOnError(error -> log.error("Error finding user by email: {}", error.getMessage()));
    }

}

