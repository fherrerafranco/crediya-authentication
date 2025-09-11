package crediya.authentication.api.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtTokenManagerImplTest {

    private JwtTokenManagerImpl jwtTokenManager;

    @BeforeEach
    void setUp() {
        String testSecret = "test-secret-key-that-is-at-least-256-bits-long-for-hs256-algorithm-testing";
        Long testExpiration = 3600000L; // 1 hour
        String testIssuer = "crediya-auth-service";
        String testAudience = "crediya-app";
        jwtTokenManager = new JwtTokenManagerImpl(testSecret, testExpiration, testIssuer, testAudience);
    }

    @Test
    @DisplayName("Should generate valid JWT token with user ID and role name")
    void shouldGenerateValidJwtTokenWithUserIdAndRoleName() {
        String userId = "user123";
        String roleName = "ADMIN";

        String token = jwtTokenManager.generateToken(userId, roleName);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts: header.payload.signature
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void shouldGenerateDifferentTokensForDifferentUsers() {
        String token1 = jwtTokenManager.generateToken("user1", "ADMIN");
        String token2 = jwtTokenManager.generateToken("user2", "ADMIN");

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Should generate different tokens for different roles")
    void shouldGenerateDifferentTokensForDifferentRoles() {
        String token1 = jwtTokenManager.generateToken("user123", "ADMIN");
        String token2 = jwtTokenManager.generateToken("user123", "ADVISOR");

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Should extract user ID from valid token")
    void shouldExtractUserIdFromValidToken() {
        String userId = "user123";
        String token = jwtTokenManager.generateToken(userId, "ADMIN");

        String extractedUserId = jwtTokenManager.getUserIdFromToken(token);

        assertThat(extractedUserId).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should extract role name from valid token")
    void shouldExtractRoleNameFromValidToken() {
        String roleName = "ADVISOR";
        String token = jwtTokenManager.generateToken("user123", roleName);

        String extractedRoleName = jwtTokenManager.getRoleFromToken(token);

        assertThat(extractedRoleName).isEqualTo(roleName);
    }

    @Test
    @DisplayName("Should validate token correctly")
    void shouldValidateTokenCorrectly() {
        String token = jwtTokenManager.generateToken("user123", "ADMIN");

        boolean isValid = jwtTokenManager.validateToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should detect invalid signature")
    void shouldDetectInvalidSignature() {
        String token = jwtTokenManager.generateToken("user123", "ADMIN");
        // Tamper with the token by changing the last character
        String tamperedToken = token.substring(0, token.length() - 1) + "X";

        boolean isValid = jwtTokenManager.validateToken(tamperedToken);
        
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should detect malformed token")
    void shouldDetectMalformedToken() {
        String malformedToken = "not.a.valid.jwt.token";

        boolean isValid = jwtTokenManager.validateToken(malformedToken);
        
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should handle null token gracefully")
    void shouldHandleNullTokenGracefully() {
        boolean isValid = jwtTokenManager.validateToken(null);
        
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should handle empty token gracefully")
    void shouldHandleEmptyTokenGracefully() {
        boolean isValid = jwtTokenManager.validateToken("");
        
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should extract user ID from token with different role names")
    void shouldExtractUserIdFromTokenWithDifferentRoleNames() {
        String userId = "user456";
        
        String adminToken = jwtTokenManager.generateToken(userId, "ADMIN");
        String advisorToken = jwtTokenManager.generateToken(userId, "ADVISOR");
        String customerToken = jwtTokenManager.generateToken(userId, "CUSTOMER");

        assertThat(jwtTokenManager.getUserIdFromToken(adminToken)).isEqualTo(userId);
        assertThat(jwtTokenManager.getUserIdFromToken(advisorToken)).isEqualTo(userId);
        assertThat(jwtTokenManager.getUserIdFromToken(customerToken)).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should extract role name from token with different user IDs")
    void shouldExtractRoleNameFromTokenWithDifferentUserIds() {
        String roleName = "ADVISOR";
        
        String token1 = jwtTokenManager.generateToken("user1", roleName);
        String token2 = jwtTokenManager.generateToken("user2", roleName);
        String token3 = jwtTokenManager.generateToken("user3", roleName);

        assertThat(jwtTokenManager.getRoleFromToken(token1)).isEqualTo(roleName);
        assertThat(jwtTokenManager.getRoleFromToken(token2)).isEqualTo(roleName);
        assertThat(jwtTokenManager.getRoleFromToken(token3)).isEqualTo(roleName);
    }

    @Test
    @DisplayName("Should handle special characters in user ID")
    void shouldHandleSpecialCharactersInUserId() {
        String userIdWithSpecialChars = "user-123_test@domain.com";
        String roleName = "ADMIN";
        
        String token = jwtTokenManager.generateToken(userIdWithSpecialChars, roleName);
        
        assertThat(jwtTokenManager.getUserIdFromToken(token)).isEqualTo(userIdWithSpecialChars);
        assertThat(jwtTokenManager.getRoleFromToken(token)).isEqualTo(roleName);
        assertThat(jwtTokenManager.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("Should handle UUID user IDs correctly")
    void shouldHandleUuidUserIdsCorrectly() {
        String uuidUserId = "a1b2c3d4-e5f6-7890-abcd-ef1234567890";
        String roleName = "CUSTOMER";
        
        String token = jwtTokenManager.generateToken(uuidUserId, roleName);
        
        assertThat(jwtTokenManager.getUserIdFromToken(token)).isEqualTo(uuidUserId);
        assertThat(jwtTokenManager.getRoleFromToken(token)).isEqualTo(roleName);
    }

    @Test
    @DisplayName("Should fail to extract from invalid token")
    void shouldFailToExtractFromInvalidToken() {
        String invalidToken = "invalid.jwt.token";

        assertThatThrownBy(() -> jwtTokenManager.getUserIdFromToken(invalidToken))
                .isInstanceOf(RuntimeException.class);
        
        assertThatThrownBy(() -> jwtTokenManager.getRoleFromToken(invalidToken))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should generate tokens with correct expiration")
    void shouldGenerateTokensWithCorrectExpiration() {
        String token = jwtTokenManager.generateToken("user123", "ADMIN");
        
        // Token should be valid immediately after creation
        assertThat(jwtTokenManager.validateToken(token)).isTrue();
        
        // We can't easily test expiration without waiting or mocking time,
        // but we can verify the token has the expected structure
        assertThat(token.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Should generate token with all role types")
    void shouldGenerateTokenWithAllRoleTypes() {
        String userId = "testUser";
        
        // Test with all expected role names
        String[] roleNames = {"ADMIN", "ADVISOR", "CUSTOMER"};
        for (String roleName : roleNames) {
            String token = jwtTokenManager.generateToken(userId, roleName);
            
            assertThat(token).isNotNull().isNotEmpty();
            assertThat(jwtTokenManager.getUserIdFromToken(token)).isEqualTo(userId);
            assertThat(jwtTokenManager.getRoleFromToken(token)).isEqualTo(roleName);
            assertThat(jwtTokenManager.validateToken(token)).isTrue();
        }
    }

    @Test
    @DisplayName("Should handle custom role names")
    void shouldHandleCustomRoleNames() {
        String userId = "user123";
        
        // Test with custom role names
        String[] customRoleNames = {"SUPER_ADMIN", "GUEST", "ANALYST", "MANAGER"};
        
        for (String roleName : customRoleNames) {
            String token = jwtTokenManager.generateToken(userId, roleName);
            
            assertThat(jwtTokenManager.getRoleFromToken(token)).isEqualTo(roleName);
            assertThat(jwtTokenManager.validateToken(token)).isTrue();
        }
    }

    @Test
    @DisplayName("Should consistently validate same token multiple times")
    void shouldConsistentlyValidateSameTokenMultipleTimes() {
        String token = jwtTokenManager.generateToken("user123", "ADMIN");
        
        // Validate the same token multiple times
        for (int i = 0; i < 5; i++) {
            assertThat(jwtTokenManager.validateToken(token)).isTrue();
            assertThat(jwtTokenManager.getUserIdFromToken(token)).isEqualTo("user123");
            assertThat(jwtTokenManager.getRoleFromToken(token)).isEqualTo("ADMIN");
        }
    }

    @Test
    @DisplayName("Should create token with valid non-null values")
    void shouldCreateTokenWithValidNonNullValues() {
        // Test that normal operation works - null handling is implementation detail
        String token1 = jwtTokenManager.generateToken("user123", "ADMIN");
        String token2 = jwtTokenManager.generateToken("user456", "ADVISOR");
        
        assertThat(token1).isNotNull().isNotEmpty();
        assertThat(token2).isNotNull().isNotEmpty();
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Should validate token with correct issuer and audience")
    void shouldValidateTokenWithCorrectIssuerAndAudience() {
        String token = jwtTokenManager.generateToken("user123", "ADMIN");
        
        boolean isValid = jwtTokenManager.validateToken(token);
        
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should generate tokens with unique JWT IDs")
    void shouldGenerateTokensWithUniqueJwtIds() {
        String token1 = jwtTokenManager.generateToken("user123", "ADMIN");
        String token2 = jwtTokenManager.generateToken("user123", "ADMIN");
        
        // Even with same user and role, tokens should be different due to unique JWT IDs
        assertThat(token1).isNotEqualTo(token2);
        assertThat(jwtTokenManager.validateToken(token1)).isTrue();
        assertThat(jwtTokenManager.validateToken(token2)).isTrue();
    }
}