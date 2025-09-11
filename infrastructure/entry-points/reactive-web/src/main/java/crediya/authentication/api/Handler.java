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
import crediya.authentication.api.mapper.UserResponseMapper;
import crediya.authentication.api.constants.LogMessages;
import crediya.authentication.api.constants.HandlerConstants;
import crediya.authentication.api.config.ErrorMessages;
import crediya.authentication.api.config.AuthorizationService;
import crediya.authentication.model.auth.Permission;
import crediya.authentication.model.exception.ValidationException;
import crediya.authentication.model.exception.BusinessRuleViolationException;
import crediya.authentication.model.auth.gateways.PasswordEncoder;
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
    private final UserResponseMapper userResponseMapper;
    private final AuthorizationService authorizationService;
    private final PasswordEncoder passwordEncoder;

    public Mono<ServerResponse> listenSaveUser(ServerRequest request) {
        log.info(LogMessages.POST_REQUEST_RECEIVED, 
                request.remoteAddress().map(addr -> addr.getAddress().getHostAddress()).orElse(HandlerConstants.UNKNOWN_ADDRESS),
                request.headers().firstHeader(HandlerConstants.USER_AGENT_HEADER));
        
        // Check authorization - only users with CREATE_USER permission
        return authorizationService.hasPermission(request.exchange(), Permission.CREATE_USER)
                .flatMap(hasPermission -> {
                    if (!hasPermission) {
                        return ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(String.format(HandlerConstants.ERROR_JSON_TEMPLATE, HandlerConstants.INSUFFICIENT_PERMISSIONS_CREATE_USERS));
                    }
                    return proceedWithUserCreation(request);
                })
                .switchIfEmpty(ServerResponse.status(HttpStatus.FORBIDDEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(String.format(HandlerConstants.ERROR_JSON_TEMPLATE, HandlerConstants.INSUFFICIENT_PERMISSIONS_CREATE_USERS)))
                .doOnError(error -> log.error(LogMessages.POST_REQUEST_ERROR, error.getMessage()));
    }

    private Mono<ServerResponse> proceedWithUserCreation(ServerRequest request) {
        
        return request.bodyToMono(UserCreateRequest.class)
                .flatMap(this::validateRequest)
                .flatMap(validRequest -> {
                    try {
                        User user = userResponseMapper.toDomain(validRequest);
                        return userUseCase.saveUser(user);
                    } catch (Exception e) {
                        return Mono.error(e);
                    }
                })
                .flatMap(savedUser -> {
                    log.info(LogMessages.USER_CREATED_SUCCESS, savedUser.getId());
                    UserResponse response = userResponseMapper.toResponse(savedUser);
                    return ServerResponse.status(HttpStatus.CREATED)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(response);
                });
    }

    public Mono<ServerResponse> listenGetAllUsers(ServerRequest request) {
        log.info(LogMessages.GET_REQUEST_RECEIVED, 
                request.remoteAddress().map(addr -> addr.getAddress().getHostAddress()).orElse(HandlerConstants.UNKNOWN_ADDRESS),
                request.headers().firstHeader(HandlerConstants.USER_AGENT_HEADER));
        
        // Check authorization - only users with VIEW_ALL_USERS permission
        return authorizationService.hasPermission(request.exchange(), Permission.VIEW_ALL_USERS)
                .flatMap(hasPermission -> {
                    if (!hasPermission) {
                        return ServerResponse.status(HttpStatus.FORBIDDEN)
                                .contentType(MediaType.APPLICATION_JSON)
                                .bodyValue(String.format(HandlerConstants.ERROR_JSON_TEMPLATE, HandlerConstants.INSUFFICIENT_PERMISSIONS_VIEW_USERS));
                    }
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(userUseCase.getAllUsers().map(userResponseMapper::toResponse), UserResponse.class)
                            .doOnSuccess(response -> log.info(LogMessages.GET_RESPONSE_SUCCESS));
                })
                .switchIfEmpty(ServerResponse.status(HttpStatus.FORBIDDEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(String.format(HandlerConstants.ERROR_JSON_TEMPLATE, HandlerConstants.INSUFFICIENT_PERMISSIONS_VIEW_USERS)))
                .doOnError(error -> log.error(LogMessages.GET_REQUEST_ERROR, error.getMessage()));
    }

    private Mono<UserCreateRequest> validateRequest(UserCreateRequest request) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, HandlerConstants.USER_CREATE_REQUEST_BINDING_NAME);
        validator.validate(request, bindingResult);
        
        if (bindingResult.hasErrors()) {
            log.info(LogMessages.VALIDATION_FAILED, bindingResult.getAllErrors());
            StringBuilder errorMessage = new StringBuilder(HandlerConstants.VALIDATION_FAILED_PREFIX);
            bindingResult.getAllErrors().forEach(error -> 
                errorMessage.append(error.getDefaultMessage()).append(HandlerConstants.VALIDATION_ERROR_SEPARATOR));
            return Mono.error(new ValidationException(errorMessage.toString()));
        }
        
        return Mono.just(request);
    }

    public Mono<ServerResponse> listenLogin(ServerRequest request) {
        log.info(HandlerConstants.LOGIN_REQUEST_RECEIVED_LOG, 
                request.remoteAddress().map(addr -> addr.getAddress().getHostAddress()).orElse(HandlerConstants.UNKNOWN_ADDRESS));
        
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
                                    .tokenType(HandlerConstants.BEARER_TOKEN_TYPE)
                                    .expiresIn(HandlerConstants.DEFAULT_TOKEN_EXPIRATION_SECONDS)
                                    .build())
                )
                .doOnSuccess(response -> log.info(HandlerConstants.USER_AUTHENTICATED_SUCCESS))
                .doOnError(error -> log.error(HandlerConstants.AUTHENTICATION_FAILED_LOG, error.getMessage()));
    }
    
    private Mono<LoginRequest> validateLoginRequest(LoginRequest request) {
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, HandlerConstants.LOGIN_REQUEST_BINDING_NAME);
        validator.validate(request, bindingResult);
        
        if (bindingResult.hasErrors()) {
            log.info(HandlerConstants.LOGIN_VALIDATION_FAILED_LOG, bindingResult.getAllErrors());
            StringBuilder errorMessage = new StringBuilder(HandlerConstants.VALIDATION_FAILED_PREFIX);
            bindingResult.getAllErrors().forEach(error -> 
                errorMessage.append(error.getDefaultMessage()).append(HandlerConstants.VALIDATION_ERROR_SEPARATOR));
            return Mono.error(new ValidationException(errorMessage.toString()));
        }
        
        return Mono.just(request);
    }
    

}
