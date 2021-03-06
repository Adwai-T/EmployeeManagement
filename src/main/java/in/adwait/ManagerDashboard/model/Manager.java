package in.adwait.ManagerDashboard.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name="managers", schema = "managers")
@Validated
@NoArgsConstructor @AllArgsConstructor
@Setter @Getter @ToString
public class Manager {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String emailId;

    @NotBlank
    private String password;

    @NotBlank
    private String role;
}
