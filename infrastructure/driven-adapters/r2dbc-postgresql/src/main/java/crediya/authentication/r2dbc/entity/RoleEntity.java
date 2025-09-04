package crediya.authentication.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("roles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoleEntity {
    
    @Id
    @Column("role_id")
    private Integer roleId;
    
    @Column("name")
    private String name;
    
    @Column("description")
    private String description;
}