package crediya.authentication.model.role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class Role {
    private final Integer id;
    private final String name;
    private final String description;
}