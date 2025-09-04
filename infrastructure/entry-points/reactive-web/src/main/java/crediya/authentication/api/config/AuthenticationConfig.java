package crediya.authentication.api.config;

import crediya.authentication.model.auth.AuthorizationService;
import crediya.authentication.model.auth.gateways.PasswordEncoder;
import crediya.authentication.model.role.gateways.RoleRepository;
import crediya.authentication.usecase.auth.LoginUseCase;
import crediya.authentication.usecase.user.UserUseCase;
import crediya.authentication.model.auth.gateways.JwtTokenManager;
import crediya.authentication.model.user.gateways.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AuthenticationConfig {
    
    @Bean
    public BCryptPasswordEncoder bcryptPasswordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    
    @Bean
    public PasswordEncoder passwordEncoder(BCryptPasswordEncoder bcryptPasswordEncoder) {
        return new PasswordEncoder() {
            @Override
            public String encode(String rawPassword) {
                return bcryptPasswordEncoder.encode(rawPassword);
            }

            @Override
            public boolean matches(String rawPassword, String encodedPassword) {
                return bcryptPasswordEncoder.matches(rawPassword, encodedPassword);
            }
        };
    }
    
    // AuthorizationService bean is now provided by @Service annotation in crediya.authentication.api.config.AuthorizationService
    
    @Bean
    public LoginUseCase loginUseCase(UserRepository userRepository, 
                                   PasswordEncoder passwordEncoder, 
                                   JwtTokenManager jwtTokenManager) {
        return new LoginUseCase(userRepository, passwordEncoder, jwtTokenManager);
    }
    
    @Bean
    public UserUseCase userUseCase(UserRepository userRepository, 
                                 RoleRepository roleRepository, 
                                 PasswordEncoder passwordEncoder) {
        return new UserUseCase(userRepository, roleRepository, passwordEncoder);
    }
}