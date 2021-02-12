package in.adwait.ManagerDashboard.Utilities;

import in.adwait.ManagerDashboard.model.Manager;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtilities {

    @Value("${jwt.secret}")
    private String JWT_SECRET;

    public String generateToken(Manager manager) {
        Map<String, Object> claims = new HashMap<>();
        return null;
    }

    private String createToken(Map<String, Object> claims, String emailId) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(emailId)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 604800000))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET)
                .compact();
    }
}
