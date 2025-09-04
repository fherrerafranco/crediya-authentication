package crediya.authentication.model.auth.gateways;

public interface JwtTokenManager {
    
    String generateToken(String userId, Integer roleId);
    
    String getUserIdFromToken(String token);
    
    Integer getRoleIdFromToken(String token);
    
    boolean validateToken(String token);
}