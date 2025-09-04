import crediya.authentication.api.config.JwtTokenManagerImpl;

public class DebugJwtTest {
    public static void main(String[] args) {
        String testSecret = "test-secret-key-that-is-at-least-256-bits-long-for-hs256-algorithm-testing";
        Long testExpiration = 3600000L; // 1 hour
        String testIssuer = "crediya-auth-service";
        String testAudience = "crediya-app";
        
        JwtTokenManagerImpl jwtTokenManager = new JwtTokenManagerImpl(testSecret, testExpiration, testIssuer, testAudience);
        
        // Generate a valid token
        String token = jwtTokenManager.generateToken("user123", 1);
        System.out.println("Original token: " + token);
        System.out.println("Original token valid: " + jwtTokenManager.validateToken(token));
        
        // Tamper with the token
        String tamperedToken = token.substring(0, token.length() - 1) + "X";
        System.out.println("Tampered token: " + tamperedToken);
        System.out.println("Tampered token valid: " + jwtTokenManager.validateToken(tamperedToken));
        
        // Try to extract claims from tampered token
        try {
            String userId = jwtTokenManager.getUserIdFromToken(tamperedToken);
            System.out.println("Extracted user ID from tampered token: " + userId);
        } catch (Exception e) {
            System.out.println("Exception when extracting from tampered token: " + e.getMessage());
        }
    }
}