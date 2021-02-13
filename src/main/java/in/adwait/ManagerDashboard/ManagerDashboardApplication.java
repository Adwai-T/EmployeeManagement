package in.adwait.ManagerDashboard;

import in.adwait.ManagerDashboard.model.Employee;
import in.adwait.ManagerDashboard.model.Manager;
import in.adwait.ManagerDashboard.repositories.EmployeeRepository;
import in.adwait.ManagerDashboard.repositories.ManagerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableJpaRepositories(basePackageClasses = { ManagerRepository.class, EmployeeRepository.class })
@EntityScan(basePackageClasses = { Manager.class, Employee.class } )
@EnableSwagger2
public class ManagerDashboardApplication {

	private EmployeeRepository repository;
	private ManagerRepository repository1;

	@Autowired
	public ManagerDashboardApplication(EmployeeRepository repository, ManagerRepository repository1) {
		this.repository = repository;
		this.repository1 = repository1;
	}

	public static void main(String[] args) {
		SpringApplication.run(ManagerDashboardApplication.class, args);
	}

}
