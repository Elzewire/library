package it.raccoon.library.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtProvider {

    @Value("${jwt.expires}")
    private long expires;

    @Value("${jwt.refresh}")
    private long refresh;

    @Value("${jwt.secret}")
    private String secret;

    public String generateAccessToken(String username, List<String> roles) {

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expires);

        Claims claims = Jwts.claims().setSubject(username);
        claims.put("roles", String.join(",", roles));

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    public String generateRefreshToken(String username) {

        Date now = new Date();
        Date expiration = new Date(now.getTime() + refresh);
        Claims claims = Jwts.claims().setSubject(username);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public Collection<SimpleGrantedAuthority> getRolesFromToken(String token) {
        return Arrays.stream(getClaimsFromToken(token).get("roles").toString().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getClaimsFromToken(token).getExpiration();
        return expiration.before(new Date());
    }
}
