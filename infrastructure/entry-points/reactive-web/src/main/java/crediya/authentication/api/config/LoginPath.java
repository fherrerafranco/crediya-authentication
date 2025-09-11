package crediya.authentication.api.config;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@ConfigurationProperties(prefix = "routes.paths")
public class LoginPath {

    private final String login;
    
    public LoginPath(String login) {
        this.login = login;
    }
}