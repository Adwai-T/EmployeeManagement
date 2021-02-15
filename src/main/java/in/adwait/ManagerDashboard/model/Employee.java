package in.adwait.ManagerDashboard.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name="employees", schema = "employees")
@Validated
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter @ToString
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String emailId;

    @NotBlank
    private String role;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long managerId;

    @NotBlank
    private String joiningDate;

    @NotBlank
    private String gender;

    @NotNull
    private Long salary;
}
