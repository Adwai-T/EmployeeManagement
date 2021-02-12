package in.adwait.ManagerDashboard.services;

import in.adwait.ManagerDashboard.configurations.ManagementRepository;
import in.adwait.ManagerDashboard.model.Manager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class ManagerService implements UserDetailsService {

    private ManagementRepository repository;

    @Autowired
    public ManagerService(ManagementRepository repository) {
        this.repository = repository;
    }

    //In our case we are going to have unique emailId used for login.
    @Override
    public UserDetails loadUserByUsername(String emailId) throws UsernameNotFoundException {
        Optional<Manager> managerOptional = repository.findByEmailId(emailId);

        if(managerOptional.isPresent()) {
            Manager manager = managerOptional.get();

            ArrayList<GrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority(manager.getRole()));

            return new User(
                    manager.getEmailId(),
                    manager.getPassword(),
                    authorities);
        }else {
            throw new UsernameNotFoundException("User with emailId " + emailId + " not found.");
        }
    }
}
