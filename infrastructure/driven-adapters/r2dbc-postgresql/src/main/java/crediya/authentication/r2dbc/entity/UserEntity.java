package crediya.authentication.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Table("users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {

    @Id
    @Column("user_id")
    private UUID id;
    @Column("first_name")
    private String firstName;
    @Column("last_name")
    private String lastName;
    @Column("email")
    private String email;
    @Column("identity_document")
    private String identityDocument;
    @Column("phone")
    private String phone;
    @Column("role_id")
    private String roleId;
    @Column("base_salary")
    private BigDecimal baseSalary;
    @Column("birth_date")
    private String birthDate;
    @Column("address")
    private String address;

}
