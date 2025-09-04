package crediya.authentication.api.config;

import crediya.authentication.model.auth.gateways.JwtTokenManager;
import io.jsonwebtoken.Claims;
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
    
    public JwtTokenManagerImpl(
            @Value("${jwt.secret:your-very-long-secret-key-here-must-be-256-bits-minimum-for-security}") String jwtSecret,
            @Value("${jwt.expiration:86400000}") Long jwtExpiration) {
        this.jwtSecret = jwtSecret;
        this.jwtExpiration = jwtExpiration;
    }

    @Override
    public String generateToken(String userId, Integer roleId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + jwtExpiration);
        
        return Jwts.builder()
                .setSubject(userId)
                .claim("roleId", roleId)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(getSigningKey())
                .compact();
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

    @Override
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}