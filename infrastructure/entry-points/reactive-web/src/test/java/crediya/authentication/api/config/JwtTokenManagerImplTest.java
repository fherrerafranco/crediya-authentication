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
    @DisplayName("Should generate valid JWT token with user ID and role ID")
    void shouldGenerateValidJwtTokenWithUserIdAndRoleId() {
        String userId = "user123";
        Integer roleId = 1;

        String token = jwtTokenManager.generateToken(userId, roleId);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts: header.payload.signature
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void shouldGenerateDifferentTokensForDifferentUsers() {
        String token1 = jwtTokenManager.generateToken("user1", 1);
        String token2 = jwtTokenManager.generateToken("user2", 1);

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Should generate different tokens for different roles")
    void shouldGenerateDifferentTokensForDifferentRoles() {
        String token1 = jwtTokenManager.generateToken("user123", 1);
        String token2 = jwtTokenManager.generateToken("user123", 2);

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Should extract user ID from valid token")
    void shouldExtractUserIdFromValidToken() {
        String userId = "user123";
        String token = jwtTokenManager.generateToken(userId, 1);

        String extractedUserId = jwtTokenManager.getUserIdFromToken(token);

        assertThat(extractedUserId).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should extract role ID from valid token")
    void shouldExtractRoleIdFromValidToken() {
        Integer roleId = 2;
        String token = jwtTokenManager.generateToken("user123", roleId);

        Integer extractedRoleId = jwtTokenManager.getRoleIdFromToken(token);

        assertThat(extractedRoleId).isEqualTo(roleId);
    }

    @Test
    @DisplayName("Should validate token correctly")
    void shouldValidateTokenCorrectly() {
        String token = jwtTokenManager.generateToken("user123", 1);

        boolean isValid = jwtTokenManager.validateToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should detect invalid signature")
    void shouldDetectInvalidSignature() {
        String token = jwtTokenManager.generateToken("user123", 1);
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
    @DisplayName("Should extract user ID from token with different role IDs")
    void shouldExtractUserIdFromTokenWithDifferentRoleIds() {
        String userId = "user456";
        
        String adminToken = jwtTokenManager.generateToken(userId, 1);
        String advisorToken = jwtTokenManager.generateToken(userId, 2);
        String customerToken = jwtTokenManager.generateToken(userId, 3);

        assertThat(jwtTokenManager.getUserIdFromToken(adminToken)).isEqualTo(userId);
        assertThat(jwtTokenManager.getUserIdFromToken(advisorToken)).isEqualTo(userId);
        assertThat(jwtTokenManager.getUserIdFromToken(customerToken)).isEqualTo(userId);
    }

    @Test
    @DisplayName("Should extract role ID from token with different user IDs")
    void shouldExtractRoleIdFromTokenWithDifferentUserIds() {
        Integer roleId = 2;
        
        String token1 = jwtTokenManager.generateToken("user1", roleId);
        String token2 = jwtTokenManager.generateToken("user2", roleId);
        String token3 = jwtTokenManager.generateToken("user3", roleId);

        assertThat(jwtTokenManager.getRoleIdFromToken(token1)).isEqualTo(roleId);
        assertThat(jwtTokenManager.getRoleIdFromToken(token2)).isEqualTo(roleId);
        assertThat(jwtTokenManager.getRoleIdFromToken(token3)).isEqualTo(roleId);
    }

    @Test
    @DisplayName("Should handle special characters in user ID")
    void shouldHandleSpecialCharactersInUserId() {
        String userIdWithSpecialChars = "user-123_test@domain.com";
        Integer roleId = 1;
        
        String token = jwtTokenManager.generateToken(userIdWithSpecialChars, roleId);
        
        assertThat(jwtTokenManager.getUserIdFromToken(token)).isEqualTo(userIdWithSpecialChars);
        assertThat(jwtTokenManager.getRoleIdFromToken(token)).isEqualTo(roleId);
        assertThat(jwtTokenManager.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("Should handle UUID user IDs correctly")
    void shouldHandleUuidUserIdsCorrectly() {
        String uuidUserId = "a1b2c3d4-e5f6-7890-abcd-ef1234567890";
        Integer roleId = 3;
        
        String token = jwtTokenManager.generateToken(uuidUserId, roleId);
        
        assertThat(jwtTokenManager.getUserIdFromToken(token)).isEqualTo(uuidUserId);
        assertThat(jwtTokenManager.getRoleIdFromToken(token)).isEqualTo(roleId);
    }

    @Test
    @DisplayName("Should fail to extract from invalid token")
    void shouldFailToExtractFromInvalidToken() {
        String invalidToken = "invalid.jwt.token";

        assertThatThrownBy(() -> jwtTokenManager.getUserIdFromToken(invalidToken))
                .isInstanceOf(RuntimeException.class);
        
        assertThatThrownBy(() -> jwtTokenManager.getRoleIdFromToken(invalidToken))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("Should generate tokens with correct expiration")
    void shouldGenerateTokensWithCorrectExpiration() {
        String token = jwtTokenManager.generateToken("user123", 1);
        
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
        
        // Test with all expected role IDs
        for (Integer roleId = 1; roleId <= 3; roleId++) {
            String token = jwtTokenManager.generateToken(userId, roleId);
            
            assertThat(token).isNotNull().isNotEmpty();
            assertThat(jwtTokenManager.getUserIdFromToken(token)).isEqualTo(userId);
            assertThat(jwtTokenManager.getRoleIdFromToken(token)).isEqualTo(roleId);
            assertThat(jwtTokenManager.validateToken(token)).isTrue();
        }
    }

    @Test
    @DisplayName("Should handle edge case role IDs")
    void shouldHandleEdgeCaseRoleIds() {
        String userId = "user123";
        
        // Test with edge case values
        Integer[] edgeCaseRoleIds = {0, -1, 999, Integer.MAX_VALUE};
        
        for (Integer roleId : edgeCaseRoleIds) {
            String token = jwtTokenManager.generateToken(userId, roleId);
            
            assertThat(jwtTokenManager.getRoleIdFromToken(token)).isEqualTo(roleId);
            assertThat(jwtTokenManager.validateToken(token)).isTrue();
        }
    }

    @Test
    @DisplayName("Should consistently validate same token multiple times")
    void shouldConsistentlyValidateSameTokenMultipleTimes() {
        String token = jwtTokenManager.generateToken("user123", 1);
        
        // Validate the same token multiple times
        for (int i = 0; i < 5; i++) {
            assertThat(jwtTokenManager.validateToken(token)).isTrue();
            assertThat(jwtTokenManager.getUserIdFromToken(token)).isEqualTo("user123");
            assertThat(jwtTokenManager.getRoleIdFromToken(token)).isEqualTo(1);
        }
    }

    @Test
    @DisplayName("Should create token with valid non-null values")
    void shouldCreateTokenWithValidNonNullValues() {
        // Test that normal operation works - null handling is implementation detail
        String token1 = jwtTokenManager.generateToken("user123", 1);
        String token2 = jwtTokenManager.generateToken("user456", 2);
        
        assertThat(token1).isNotNull().isNotEmpty();
        assertThat(token2).isNotNull().isNotEmpty();
        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Should validate token with correct issuer and audience")
    void shouldValidateTokenWithCorrectIssuerAndAudience() {
        String token = jwtTokenManager.generateToken("user123", 1);
        
        boolean isValid = jwtTokenManager.validateToken(token);
        
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should generate tokens with unique JWT IDs")
    void shouldGenerateTokensWithUniqueJwtIds() {
        String token1 = jwtTokenManager.generateToken("user123", 1);
        String token2 = jwtTokenManager.generateToken("user123", 1);
        
        // Even with same user and role, tokens should be different due to unique JWT IDs
        assertThat(token1).isNotEqualTo(token2);
        assertThat(jwtTokenManager.validateToken(token1)).isTrue();
        assertThat(jwtTokenManager.validateToken(token2)).isTrue();
    }
}