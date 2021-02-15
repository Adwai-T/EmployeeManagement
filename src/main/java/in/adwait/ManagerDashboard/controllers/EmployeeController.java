package in.adwait.ManagerDashboard.controllers;

import in.adwait.ManagerDashboard.Utilities.JwtUtilities;
import in.adwait.ManagerDashboard.model.Employee;
import in.adwait.ManagerDashboard.model.ExceptionMessages;
import in.adwait.ManagerDashboard.model.Manager;
import in.adwait.ManagerDashboard.repositories.EmployeeRepository;
import in.adwait.ManagerDashboard.repositories.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/employee")
@CrossOrigin(origins = "http://localhost:4200")
public class EmployeeController {

    private EmployeeRepository employeeRepository;
    private JwtUtilities jwtUtilities;
    private ManagerRepository managerRepository;

    @Autowired
    public EmployeeController(
            EmployeeRepository employeeRepository,
            JwtUtilities jwtUtilities,
            ManagerRepository managerRepository) {
        this.employeeRepository = employeeRepository;
        this.jwtUtilities = jwtUtilities;
        this.managerRepository = managerRepository;
    }

    @GetMapping("")
    private ResponseEntity getEmployee(
            @RequestParam(value = "id", defaultValue = "") Long id,
            @RequestParam(value = "email_id", defaultValue = "") String emailId,
            @RequestParam(value = "manager_email_id", defaultValue = "") String managerEmailId,
            @RequestParam(value = "manager_id", defaultValue = "") Long managerId,
            @RequestParam(value = "page", defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "size", defaultValue = "0") Integer size
    ) {
        if(id != null && !id.equals("")){
            Optional<Employee> employee = employeeRepository.findById(id);

            if(employee.isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(employee.get());
            }
        }

        if(emailId != null && !emailId.equals("")){
            Optional<Employee> employee = employeeRepository.findByEmailId(emailId);

            if(employee.isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(employee.get());
            }
        }

        if(managerEmailId != null && !managerEmailId.equals("")) {
            Optional<Manager> manager = managerRepository.findByEmailId(managerEmailId);
            if(manager.isPresent()) {
                Optional<List<Employee>> employees = employeeRepository.findAllByManagerId(manager.get().getId());

                if(employees.isPresent()) {
                    return ResponseEntity
                                .status(HttpStatus.OK)
                                .body(employees);
                }else {
                    return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(null);
                }
            }
        }

        if(managerId != null) {
            Optional<List<Employee>> employees;
            if(size != 0) {
                 employees = employeeRepository.findAllByManagerId(managerId, PageRequest.of(pageNumber, size));
            }else {
                employees = employeeRepository.findAllByManagerId(managerId);
            }

            if(employees.isPresent()) {
                return ResponseEntity
                            .status(HttpStatus.OK)
                            .body(employees);
            }
        }

        if((id == null && managerId == null) || (id.equals("") && managerId.equals(""))) {
            List<Employee> employees = employeeRepository.findAll();

            return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(employees);
        }

        return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(null);
    }

    @PostMapping("")
    private ResponseEntity<Employee> postEmployee(
            @Valid @RequestBody Employee employee,
            @RequestHeader(value = "Authorization", defaultValue = "") String authToken
    ){
        if(authToken.startsWith("Bearer ")) {

            String jwt = authToken.substring(7);

            Optional<Employee> employeeOptional = employeeRepository.findByEmailId(employee.getEmailId());

            try{
                if(employeeOptional.isEmpty()) {
                    //-------Check
                    String managerId = jwtUtilities.extractId(jwt);
                    employee.setManagerId(Long.parseLong(managerId));

                    employeeRepository.save(employee);

                    return ResponseEntity
                            .status(HttpStatus.CREATED)
                            .body(employee);
                }
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(employeeOptional.get());
            }catch (Exception e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
    }

    @PatchMapping("")
    private ResponseEntity<Employee> updateEmployee(
            @Valid @RequestBody Employee updatedEmployee
    ){
        Optional<Employee> employee = employeeRepository.findByEmailId(updatedEmployee.getEmailId());

        try{
            if(employee.isPresent()) {
                employeeRepository.delete(employee.get());
                employeeRepository.save(updatedEmployee);

                return ResponseEntity
                        .status(HttpStatus.ACCEPTED)
                        .body(updatedEmployee);
            }else {
                return ResponseEntity
                            .status(HttpStatus.CONFLICT)
                            .body(null);
            }
        }catch (Exception e) { }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(null);

    }



    @PutMapping("")
    private ResponseEntity<Employee> updateOrAddEmployee(
            @Valid @RequestBody Employee updatedEmployee
    ){
        Optional<Employee> employee = employeeRepository.findByEmailId(updatedEmployee.getEmailId());

        try{
            if(employee.isPresent()) {
                employeeRepository.delete(employee.get());
            }
            employeeRepository.save(updatedEmployee);

            return ResponseEntity
                        .status(HttpStatus.ACCEPTED)
                        .body(updatedEmployee);
        }catch (Exception e) { }

        return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);

    }

    @DeleteMapping("")
    private ResponseEntity<String> deleteEmployee(
            @RequestParam(value = "id", defaultValue = "") Long id
    ){
        if(id != null && !id.equals("")) {

            try{
                employeeRepository.deleteById(id);

                return ResponseEntity
                        .status(HttpStatus.ACCEPTED)
                        .body("Employee with id " + id + " deleted successfully.");
            }catch (Exception e) { }
        }
        return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("No such Employee found. Could not delete employee");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionMessages> hasException() {

        return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ExceptionMessages("NoSuchElementException", "No Such Employee Found"));
    }
}
