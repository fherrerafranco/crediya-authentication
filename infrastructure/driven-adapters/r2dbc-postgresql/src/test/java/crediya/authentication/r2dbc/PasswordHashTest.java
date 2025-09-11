package crediya.authentication.r2dbc;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashTest {
    
    @Test
    public void testPasswordHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String hash = "$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3bp.Erzi/m";
        
        // Test common passwords
        String[] passwords = {"secret123", "password", "admin", "123456", "password123", "admin123", "test123"};
        
        System.out.println("Testing existing hash: " + hash);
        for (String pwd : passwords) {
            boolean matches = encoder.matches(pwd, hash);
            System.out.println("Password '" + pwd + "' matches: " + matches);
        }
        
        // Generate new hashes with strength 12 for known passwords
        System.out.println("\n=== Generating new hashes ===");
        String[] knownPasswords = {"password123", "admin123", "test123"};
        for (String pwd : knownPasswords) {
            String newHash = encoder.encode(pwd);
            System.out.println("Password: '" + pwd + "'");
            System.out.println("Hash: " + newHash);
            System.out.println("Matches: " + encoder.matches(pwd, newHash));
            System.out.println("---");
        }
    }
}