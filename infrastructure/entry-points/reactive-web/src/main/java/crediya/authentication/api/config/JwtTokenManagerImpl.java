package crediya.authentication.api.config;

import crediya.authentication.model.auth.gateways.JwtTokenManager;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenManagerImpl implements JwtTokenManager {
    
    private final String jwtSecret;
    private final Long jwtExpiration;
    private final String jwtIssuer;
    private final String jwtAudience;
    
    public JwtTokenManagerImpl(
            @Value("${jwt.secret}") String jwtSecret,
            @Value("${jwt.expiration}") Long jwtExpiration,
            @Value("${jwt.issuer:crediya-auth-service}") String jwtIssuer,
            @Value("${jwt.audience:crediya-app}") String jwtAudience) {
        this.jwtSecret = jwtSecret;
        this.jwtExpiration = jwtExpiration;
        this.jwtIssuer = jwtIssuer;
        this.jwtAudience = jwtAudience;
    }

    @Override
    public String generateToken(String userId, Integer roleId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtExpiration);
        
        return Jwts.builder()
                .subject(userId)
                .claim("roleId", roleId)
                .issuer(jwtIssuer)
                .audience().add(jwtAudience).and()  // Fixed: added .and() to return to JwtBuilder
                .issuedAt(now)
                .expiration(validity)
                .id(java.util.UUID.randomUUID().toString())
                .signWith(getSigningKey())
                .compact();
    }
    
    @Override
    public boolean validateToken(String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                return false;
            }
            
            // This will throw JwtException if signature is invalid
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            
            return !claims.getExpiration().before(new Date()) &&
                   jwtIssuer.equals(claims.getIssuer()) &&
                   claims.getAudience().contains(jwtAudience);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    @Override
    public String getUserIdFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    @Override
    public Integer getRoleIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("roleId", Integer.class);
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())  // Updated from setSigningKey
                .build()
                .parseSignedClaims(token)     // Updated from parseClaimsJws
                .getPayload();                // Updated from getBody
    }
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}