import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class DebugPasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        
        // Test the exact hash from database
        String dbHash = "$2a$12$LQv3c1yqBwEHxPuNYkFNWONSLsxcactOHsSCLVxO2Qa1A6Mm2XlvG";
        String password = "secret123";
        
        System.out.println("Testing password: " + password);
        System.out.println("Against hash: " + dbHash);
        System.out.println("Match result: " + encoder.matches(password, dbHash));
        
        // Test with "password"
        String passwordHash = "$2a$12$4CatIBj8.S8GYmFOw8/5XuYjdZ8Pt9SLyqyA5lYjkZjkZjkZjkZjk";
        System.out.println("\nTesting password: password");
        System.out.println("Against hash: " + passwordHash);
        System.out.println("Match result: " + encoder.matches("password", passwordHash));
        
        // Generate a fresh hash for secret123
        String freshHash = encoder.encode("secret123");
        System.out.println("\nFresh hash for secret123: " + freshHash);
        System.out.println("Fresh hash matches: " + encoder.matches("secret123", freshHash));
    }
}