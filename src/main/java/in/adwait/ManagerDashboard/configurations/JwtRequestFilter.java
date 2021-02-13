package in.adwait.ManagerDashboard.configurations;

import in.adwait.ManagerDashboard.Utilities.JwtUtilities;
import in.adwait.ManagerDashboard.services.ManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private JwtUtilities jwtUtil;
    private ManagerService managerService;

    @Autowired
    public JwtRequestFilter(JwtUtilities jwtUtil, ManagerService managerService) {
        this.jwtUtil = jwtUtil;
        this.managerService = managerService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            FilterChain filterChain)
            throws ServletException, IOException
    {
        //Should be formatted as "Authorization" : "Bearer Jwt"
        final String authorizationHeader = httpServletRequest.getHeader("Authorization");

        String emailId = null;
        String jwt = null;

        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            emailId = jwtUtil.extractEmailId(jwt);
        }

        if(emailId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = managerService.loadUserByUsername(emailId);

            UsernamePasswordAuthenticationToken userAuthToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

            userAuthToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(httpServletRequest)
            );

            SecurityContextHolder.getContext().setAuthentication(userAuthToken);
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse)   ;
    }
}
