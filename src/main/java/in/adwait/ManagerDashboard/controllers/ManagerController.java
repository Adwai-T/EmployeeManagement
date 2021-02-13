package in.adwait.ManagerDashboard.controllers;

import in.adwait.ManagerDashboard.Utilities.JwtUtilities;
import in.adwait.ManagerDashboard.model.Manager;
import in.adwait.ManagerDashboard.repositories.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/manager")
public class ManagerController {

    private ManagerRepository repository;
    private JwtUtilities jwtUtils;

    @Autowired
    public ManagerController(ManagerRepository repository, JwtUtilities jwtUtils) {
        this.repository = repository;
        this.jwtUtils = jwtUtils;
    }

    @GetMapping("")
    public ResponseEntity<Manager> getManager(
            @RequestHeader(value = "Authorization", defaultValue = "")
            String authorizationToken
    ){

        if(authorizationToken != null && authorizationToken.startsWith("Bearer ")) {
            final String jwt = authorizationToken.substring(7);

            Optional<Manager> manager = repository.findByEmailId(jwtUtils.extractEmailId(jwt));

            if(manager.isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(manager.get());
            }
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(null);
    }

    @GetMapping("{id}")
    public ResponseEntity<Manager> getManagerById(
            @PathVariable String id
    ){
        try{
            Long mangerId = Long.parseLong(id);
            Optional<Manager> manager = repository.findById(mangerId);

            if(manager.isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(manager.get());
            }
        }catch (NumberFormatException  e) {}

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(null);
    }

    @PutMapping("update")
    public ResponseEntity<Manager> updateManager(
            @Valid  @RequestBody Manager manager
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

    @PostMapping("create")
    public ResponseEntity<Manager> createManager(
            @Valid @RequestBody Manager manager
    ){
        Optional<Manager> optionalManager = repository.findByEmailId(manager.getEmailId());
        try {
            if(optionalManager.isEmpty()) {

//                manager.setRole("ROLE_PENDING");
                repository.save(manager);

                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(manager);
            } else {
                return ResponseEntity
                            .status(HttpStatus.CONFLICT)
                            .body(optionalManager.get());
            }
        }catch (Exception e) { }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(null);
    }

    @DeleteMapping("")
    public ResponseEntity<Manager> deleteManager(
            @RequestHeader(value = "Authorization", defaultValue = "") String authToken
    ){
        if( authToken != null &&  authToken.startsWith("Bearer ")) {
            final String jwt = authToken.substring(7);
            final String emailId = jwtUtils.extractEmailId(jwt);

            Optional<Manager> manager = repository.findByEmailId(emailId);

            if(manager.isPresent()) {
                try{
                    repository.delete(manager.get());
                    return  ResponseEntity
                                .status(HttpStatus.ACCEPTED)
                                .body(manager.get());

                }catch (Exception e) { }
            }
        }
        return  ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(null);
    }

//    @ExceptionHandler({ NoSuchElementException.class })
//    public ResponseEntity<ExceptionMessages> noSuchElementException() {
//        return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body(new ExceptionMessages(
//                            "ManagerNotFound",
//                            "Manager could not be found in database"
//                    ));
//    }
}
