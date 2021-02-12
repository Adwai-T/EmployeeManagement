package in.adwait.ManagerDashboard.controllers;

import in.adwait.ManagerDashboard.configurations.ManagementRepository;
import in.adwait.ManagerDashboard.model.ExceptionMessages;
import in.adwait.ManagerDashboard.model.Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.NoSuchElementException;

@Controller
@RequestMapping("api/v1")
public class ManagerController {

    private ManagementRepository repository;

    @Autowired
    public ManagerController(ManagementRepository repository) {
        this.repository = repository;
    }

    @GetMapping("manager")
    public ResponseEntity<Manager> getManager(
            @RequestHeader(value = "Authorization", defaultValue = "")
            String authorizationToken
    ){
//        if(!authorizationToken.equals("")) {
//            //We add `bearer ` before the jwt that needs to be removed
//            String jwt = authorizationToken.substring(7);
//        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(null);
    }

    @PatchMapping("manager")
    public ResponseEntity<Manager> updateManager(
            @Valid  @RequestBody Manager manager
    ){
        return null;
    }

    @PostMapping("manager")
    public ResponseEntity<Manager> createManager(
            @Valid @RequestBody Manager manager
    ){
        try {
            if(repository.findByEmailId(manager.getEmailId()).isEmpty()) {
                repository.save(manager);

                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(manager);
            }
        }catch (Exception e) {
System.out.println(e.getMessage());
e.printStackTrace();
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(null);
    }

    @DeleteMapping("manager")
    public ResponseEntity<Manager> deleteManager(
            @RequestParam(defaultValue = "") String email
    ){
        if(!email.equals("")) {
            Manager manager = repository.findByEmailId(email).get();

            if(!manager.equals(null)) {
                try{
                    repository.delete(manager);
                    return  ResponseEntity
                                .status(HttpStatus.ACCEPTED)
                                .body(manager);

                }catch (Exception e) {
System.out.println(e.getMessage());
e.printStackTrace();
                }
            }
        }
        return  ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(null);
    }

    @ExceptionHandler({ NoSuchElementException.class })
    public ResponseEntity<ExceptionMessages> noSuchElementException() {
        return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ExceptionMessages(
                            "ManagerNotFound",
                            "Manager could not be found in database"
                    ));
    }
}
