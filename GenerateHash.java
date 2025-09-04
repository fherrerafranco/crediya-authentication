import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerateHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String password = "secret123";
        String hash = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash (strength 12): " + hash);
        
        // Verify the hash works
        boolean matches = encoder.matches(password, hash);
        System.out.println("Verification: " + matches);
    }
}