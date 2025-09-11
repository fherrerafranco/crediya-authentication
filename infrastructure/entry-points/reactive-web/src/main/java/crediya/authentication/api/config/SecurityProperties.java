package crediya.authentication.api.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
@ConfigurationProperties(prefix = "security.jwt")
public class SecurityProperties {
    private List<String> publicPaths = new ArrayList<>();
}