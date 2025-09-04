import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestBCrypt {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        String password = "secret123";
        
        // Generate a new hash
        String newHash = encoder.encode(password);
        System.out.println("Generated hash: " + newHash);
        
        // Test the existing hash from database
        String existingHash = "$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj3bp.Erzi/m";
        boolean matchesExisting = encoder.matches(password, existingHash);
        System.out.println("Password 'secret123' matches existing hash: " + matchesExisting);
        
        // Test the new hash
        boolean matchesNew = encoder.matches(password, newHash);
        System.out.println("Password 'secret123' matches new hash: " + matchesNew);
        
        // Test other common passwords against existing hash
        String[] testPasswords = {"admin123", "password", "test", "hello", "secret"};
        for (String testPwd : testPasswords) {
            boolean matches = encoder.matches(testPwd, existingHash);
            System.out.println("Password '" + testPwd + "' matches existing hash: " + matches);
        }
    }
}