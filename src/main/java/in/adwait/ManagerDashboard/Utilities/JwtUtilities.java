package in.adwait.ManagerDashboard.Utilities;

import in.adwait.ManagerDashboard.model.Manager;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtilities {

    @Value("${jwt.secret}")
    private String JWT_SECRET;

    /**
     * Generates A new JWT with Managers emailId and JWT secret from properties file.
     * @param manager
     * @return JWT
     */
    public String generateToken(Manager manager) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("name", manager.getName());
        claims.put("id", manager.getId());
        return createToken(claims, manager.getEmailId());
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

    /**
     * Returns If the given JWT is valid for the given Manager.
     * @param token
     * @param manager
     * @return
     */
    public boolean validateToken(String token, Manager manager) {
        final  String emailId = extractEmailId(token);
        return (emailId.equals(manager.getEmailId()) && !isTokenExpired(token));
    }

    /**
     * Return If the given JWT is valid for the given Manager
     * @param token
     * @param userDetails
     * @return
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String emailId = extractEmailId(token);
        return (emailId.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private Claims getClaims(String token) {
        return Jwts
                .parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Get emailId form token, Does not check if the token is valid.
     * @param token
     * @return
     */
    public String extractEmailId(String token) {
        return getClaims(token).getSubject();
    }

    private Claims parseClaims(String token){
        return Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody();
    }

    /**
     * Get manager id stored in the jwt token
     * @param token
     * @return
     */
    public String extractId(String token) {
        return parseClaims(token).get("id").toString();
    }

    /**
     * Check if given JWT has expired, does not check if the token is valid.
     * @param token
     * @return
     */
    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }
}
