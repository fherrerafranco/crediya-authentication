package crediya.authentication.model.user;

import crediya.authentication.model.valueobjects.Email;
import crediya.authentication.model.valueobjects.Salary;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private String id;
    private String firstName;
    private String lastName;
    private Email email;
    private String identityDocument;
    private String phone;
    private String roleId;
    private Salary baseSalary;
    private String birthDate;
    private String address;
}
