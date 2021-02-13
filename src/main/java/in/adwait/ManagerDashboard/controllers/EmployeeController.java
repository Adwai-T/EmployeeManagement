package in.adwait.ManagerDashboard.controllers;

import in.adwait.ManagerDashboard.Utilities.JwtUtilities;
import in.adwait.ManagerDashboard.model.Employee;
import in.adwait.ManagerDashboard.repositories.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("api/v1/employee")
public class EmployeeController {

    private EmployeeRepository employeeRepository;
    private JwtUtilities jwtUtilities;

    @Autowired
    public EmployeeController(EmployeeRepository employeeRepository, JwtUtilities jwtUtilities) {
        this.employeeRepository = employeeRepository;
        this.jwtUtilities = jwtUtilities;
    }

    @GetMapping("")
    private ResponseEntity getEmployee(
            @RequestParam(value = "id", defaultValue = "") Long id,
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

        if(managerId != null && !managerId.equals("")) {
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
                    System.out.println(jwt);
                    String managerId = jwtUtilities.extractId(jwt);
                    System.out.println("ManagerId post Mapping  -> " + managerId);
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

    @PutMapping("")
    private ResponseEntity<Employee> updateEmployee(
            @Valid @RequestBody Employee updatedEmployee
    ){
        Optional<Employee> employee = employeeRepository.findById(updatedEmployee.getId());

        try{
            if(employee.isPresent()) {
                updatedEmployee.setId(employee.get().getId());
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
            }catch (NoSuchElementException e) { }
        }
        return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("No such Employee found. Could not delete employee");
    }

//    @ExceptionHandler(NoSuchElementException.class)
//    public ResponseEntity<ExceptionMessages> noSuchElementException() {
//
//        return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND)
//                    .body(new ExceptionMessages("NoSuchElementException", "No Such Emploee Found"));
//    }
}