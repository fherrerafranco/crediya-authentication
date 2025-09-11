package crediya.authentication.model.auth.gateways;

public interface JwtTokenManager {
    
    String generateToken(String userId, String roleName);
    
    String getUserIdFromToken(String token);
    
    String getRoleFromToken(String token);
    
    boolean validateToken(String token);
}