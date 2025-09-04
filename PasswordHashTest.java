import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = "$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3bp.Erzi/m";
        
        // Test common passwords
        String[] passwords = {"secret123", "password", "admin", "123456", "password123", "admin123"};
        
        System.out.println("Testing hash: " + hash);
        for (String pwd : passwords) {
            boolean matches = encoder.matches(pwd, hash);
            System.out.println("Password '" + pwd + "' matches: " + matches);
        }
        
        // Generate a new hash for "secret123" to compare
        String newHash = encoder.encode("secret123");
        System.out.println("\nNew hash for 'secret123': " + newHash);
        System.out.println("New hash matches 'secret123': " + encoder.matches("secret123", newHash));
    }
}