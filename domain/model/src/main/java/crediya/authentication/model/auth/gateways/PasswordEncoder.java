package crediya.authentication.model.auth.gateways;

public interface PasswordEncoder {
    
    String encode(String rawPassword);
    
    boolean matches(String rawPassword, String encodedPassword);
}