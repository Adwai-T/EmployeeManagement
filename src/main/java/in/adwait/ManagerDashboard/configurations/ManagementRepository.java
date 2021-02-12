package in.adwait.ManagerDashboard.configurations;

import in.adwait.ManagerDashboard.model.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ManagementRepository extends JpaRepository<Manager, String> {

    public Optional<Manager> findByEmailId(String emailId);
}
