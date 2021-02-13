package in.adwait.ManagerDashboard.controllers;

import in.adwait.ManagerDashboard.Utilities.JwtUtilities;
import in.adwait.ManagerDashboard.model.AuthenticationDetails;
import in.adwait.ManagerDashboard.model.AuthorizationResponse;
import in.adwait.ManagerDashboard.model.Manager;
import in.adwait.ManagerDashboard.repositories.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("authenticate")
public class AuthenticationController {

    private AuthenticationManager authManager;
    private ManagerRepository repository;
    private JwtUtilities jwtUtils;

    @Autowired
    public AuthenticationController(
            AuthenticationManager authManager,
            ManagerRepository repository,
            JwtUtilities jwtUtils
    ) {
        this.authManager = authManager;
        this.repository = repository;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("")
    public ResponseEntity<AuthorizationResponse> authenticateManager(@Valid @RequestBody AuthenticationDetails authDetails) {
        try{
            //Throws BadCredential Exception if the emailId and password do not match.
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authDetails.getEmailId(), authDetails.getPassword()));

            Optional<Manager> managerOptional = repository.findByEmailId(authDetails.getEmailId());

            if(managerOptional.isPresent()) {
                final Manager manager = managerOptional.get();
                final String jwt = jwtUtils.generateToken(manager);

                return ResponseEntity
                            .status(HttpStatus.OK)
                            .body(new AuthorizationResponse(jwt));
            }else  {
                return ResponseEntity
                            .status(HttpStatus.NOT_FOUND)
                            .body(null);
            }
        }catch (BadCredentialsException e) {
            return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)
                        .body(null);
        }
    }
}
