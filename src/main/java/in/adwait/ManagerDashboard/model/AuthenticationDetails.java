package in.adwait.ManagerDashboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
public class AuthenticationDetails {

    @NotNull(message = "EmailId cannot be null.")
    @NotBlank(message = "EmailId cannot be blank")
    private String emailId;

    @NotNull(message = "Password cannot be null.")
    @NotBlank(message = "Password cannot be blank.")
    private String password;

}
