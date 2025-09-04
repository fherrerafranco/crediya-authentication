package crediya.authentication.api;

import crediya.authentication.usecase.user.UserUseCase;
import crediya.authentication.usecase.auth.LoginUseCase;
import crediya.authentication.model.user.User;
import crediya.authentication.model.auth.LoginCredentials;
import crediya.authentication.model.valueobjects.Email;
import crediya.authentication.api.dto.UserCreateRequest;
import crediya.authentication.api.dto.UserResponse;
import crediya.authentication.api.dto.LoginRequest;
import crediya.authentication.api.dto.LoginResponse;
import crediya.authentication.api.mapper.UserMapper;
import crediya.authentication.api.constants.LogMessages;
import crediya.authentication.model.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {
    private final UserUseCase userUseCase;
    private final LoginUseCase loginUseCase;
    private final Validator validator;
    private final UserMapper userMapper;

    public Mono<ServerResponse> listenSaveUser(ServerRequest request) {
        log.info(LogMessages.POST_REQUEST_RECEIVED, 
                request.remoteAddress().map(addr -> addr.getAddress().getHostAddress()).orElse("unknown"),
                request.headers().firstHeader("User-Agent"));
        
        return request.bodyToMono(UserCreateRequest.class)
                .flatMap(this::validateRequest)
                .flatMap(validRequest -> {
                    try {
                        User user = userMapper.toDomain(validRequest);
                        return userUseCase.saveUser(user);
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                })
                .flatMap(savedUser -> {
                    log.info(LogMessages.USER_CREATED_SUCCESS, savedUser.getId());
                    UserResponse response = userMapper.toResponse(savedUser);
                    return ServerResponse.status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                })
                .doOnError(error -> log.error(LogMessages.POST_REQUEST_ERROR, error.getMessage()));
    }

    public Mono<ServerResponse> listenGetAllUsers(ServerRequest request) {
        log.info(LogMessages.GET_REQUEST_RECEIVED, 
                request.remoteAddress().map(addr -> addr.getAddress().getHostAddress()).orElse("unknown"),
                request.headers().firstHeader("User-Agent"));
        
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(userUseCase.getAllUsers().map(userMapper::toResponse), UserResponse.class)
                .doOnSuccess(response -> log.info(LogMessages.GET_RESPONSE_SUCCESS))
                .doOnError(error -> log.error(LogMessages.GET_REQUEST_ERROR, error.getMessage()));
    }

    private Mono<UserCreateRequest> validateRequest(UserCreateRequest request) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, "userCreateRequest");
        validator.validate(request, bindingResult);
        
        if (bindingResult.hasErrors()) {
            log.info(LogMessages.VALIDATION_FAILED, bindingResult.getAllErrors());
            StringBuilder errorMessage = new StringBuilder("Validation failed: ");
            bindingResult.getAllErrors().forEach(error -> 
                errorMessage.append(error.getDefaultMessage()).append("; "));
            return Mono.error(new ValidationException(errorMessage.toString()));
        }
        
        return Mono.just(request);
    }

    public Mono<ServerResponse> listenLogin(ServerRequest request) {
        log.info("Login request received from: {}", 
                request.remoteAddress().map(addr -> addr.getAddress().getHostAddress()).orElse("unknown"));
        
        return request.bodyToMono(LoginRequest.class)
                .flatMap(this::validateLoginRequest)
                .flatMap(loginRequest -> {
                    LoginCredentials credentials = new LoginCredentials(
                            Email.of(loginRequest.getEmail()),
                            loginRequest.getPassword()
                    );
                    return loginUseCase.authenticate(credentials);
                })
                .flatMap(authResult -> 
                    ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(LoginResponse.builder()
                                    .token(authResult.getToken())
                                    .tokenType("Bearer")
                                    .expiresIn(86400L)
                                    .build())
                )
                .doOnSuccess(response -> log.info("User authenticated successfully"))
                .doOnError(error -> log.error("Authentication failed: {}", error.getMessage()));
    }
    
    private Mono<LoginRequest> validateLoginRequest(LoginRequest request) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, "loginRequest");
        validator.validate(request, bindingResult);
        
        if (bindingResult.hasErrors()) {
            log.info("Login validation failed: {}", bindingResult.getAllErrors());
            StringBuilder errorMessage = new StringBuilder("Validation failed: ");
            bindingResult.getAllErrors().forEach(error -> 
                errorMessage.append(error.getDefaultMessage()).append("; "));
            return Mono.error(new ValidationException(errorMessage.toString()));
        }
        
        return Mono.just(request);
    }
}
