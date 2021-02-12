package in.adwait.ManagerDashboard.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.NotNull;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Entity
@Validated
@NoArgsConstructor @AllArgsConstructor
@Setter @Getter @ToString
public class Manager {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    Integer id;

    @NotNull @NotBlank
    String name;

    @NotNull @NotBlank
    String emailId;

    @NotNull @NotBlank
    String password;

    @NotNull @NotBlank
    String role;
}
