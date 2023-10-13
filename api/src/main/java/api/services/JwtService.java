package api.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    @Value("${jwt.token.secret}")
    private String secret;
    @Value("${jwt.token.expires_in}")
    private int expiresIn;

    public String generateToken(UserDetails details) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + this.expiresIn);

        return Jwts.builder()
                .setSubject(details.getUsername())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(this.getSignWithKey())
                .compact();
    }

    public String getTokenSubject(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(this.getSignWithKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getSubject();
        } catch (SignatureException ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    public boolean tokenIsExpired(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(this.getSignWithKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.getExpiration().before(new Date());
        } catch (SignatureException ex) {
            log.error(ex.getMessage());
            return true;
        }
    }

    public boolean tokenIsValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(this.getSignWithKey()).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error(ex.getMessage());
            return false;
        }
    }

    private Key getSignWithKey() {
        byte[] bytes = this.secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(bytes);
    }
}
