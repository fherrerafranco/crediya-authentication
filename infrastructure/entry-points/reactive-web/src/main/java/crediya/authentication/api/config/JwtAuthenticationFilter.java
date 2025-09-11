package crediya.authentication.api.config;

import crediya.authentication.model.auth.gateways.JwtTokenManager;
import crediya.authentication.api.constants.JwtConstants;
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
        
        if (authHeader == null || !authHeader.startsWith(JwtConstants.BEARER_PREFIX)) {
            return unauthorized(exchange);
        }
        
        String token = authHeader.substring(JwtConstants.BEARER_PREFIX.length());
        
        if (!jwtTokenManager.validateToken(token)) {
            return unauthorized(exchange);
        }
        
        // Add user context to exchange attributes
        String userId = jwtTokenManager.getUserIdFromToken(token);
        String roleName = jwtTokenManager.getRoleFromToken(token);
        
        exchange.getAttributes().put(JwtConstants.USER_ID_ATTRIBUTE, userId);
        exchange.getAttributes().put(JwtConstants.ROLE_ATTRIBUTE, roleName);
        
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
            JwtConstants.UNAUTHORIZED_JSON_TEMPLATE,
            JwtConstants.TIMESTAMP_FIELD, LocalDateTime.now(),
            JwtConstants.STATUS_FIELD, JwtConstants.UNAUTHORIZED_STATUS_CODE,
            JwtConstants.ERROR_FIELD, JwtConstants.UNAUTHORIZED_ERROR,
            JwtConstants.MESSAGE_FIELD, JwtConstants.ACCESS_DENIED_MESSAGE
        );
        
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(errorResponse.getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
}