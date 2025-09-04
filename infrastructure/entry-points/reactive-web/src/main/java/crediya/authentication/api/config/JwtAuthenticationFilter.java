package crediya.authentication.api.config;

import crediya.authentication.model.auth.gateways.JwtTokenManager;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements WebFilter {
    
    private final JwtTokenManager jwtTokenManager;
    private final SecurityProperties securityProperties;
    private static final String BEARER_PREFIX = "Bearer ";
    
    public JwtAuthenticationFilter(JwtTokenManager jwtTokenManager, SecurityProperties securityProperties) {
        this.jwtTokenManager = jwtTokenManager;
        this.securityProperties = securityProperties;
    }
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        
        // Skip authentication for public endpoints
        if (isPublicPath(path)) {
            return chain.filter(exchange);
        }
        
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return unauthorized(exchange);
        }
        
        String token = authHeader.substring(BEARER_PREFIX.length());
        
        if (!jwtTokenManager.validateToken(token)) {
            return unauthorized(exchange);
        }
        
        // Add user context to exchange attributes
        String userId = jwtTokenManager.getUserIdFromToken(token);
        Integer roleId = jwtTokenManager.getRoleIdFromToken(token);
        
        exchange.getAttributes().put("userId", userId);
        exchange.getAttributes().put("roleId", roleId);
        
        return chain.filter(exchange);
    }
    
    private boolean isPublicPath(String path) {
        for (String publicPath : securityProperties.getPublicPaths()) {
            if (path.equals(publicPath)) {
                return true;
            }
        }
        return false;
    }
    
    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        exchange.getResponse().getHeaders().add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        
        String errorResponse = String.format(
            "{\"timestamp\":\"%s\",\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Access denied. Valid JWT token required.\"}",
            LocalDateTime.now()
        );
        
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(errorResponse.getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}