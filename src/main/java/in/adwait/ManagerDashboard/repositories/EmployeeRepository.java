package in.adwait.ManagerDashboard.repositories;

import in.adwait.ManagerDashboard.model.Employee;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmailId(String emailId);
    Optional<List<Employee>> findAllByManagerId(Long managerId);
    Optional<List<Employee>> findAllByManagerId(Long managerId, PageRequest of);
}

