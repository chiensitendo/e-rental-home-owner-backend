package com.e_rental.owner.security;

import com.e_rental.owner.entities.Users;
import com.e_rental.owner.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class UserAuthenticationProvider {

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    private final UserRepository userRepository;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String userName) {
        Claims claims = Jwts.claims().setSubject(userName);

        Date now = new Date();
        Date validity = new Date(now.getTime() + SecurityConstants.EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String resolveToken(HttpServletRequest httpServletRequest) {
        String bearerToken = httpServletRequest.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Authentication validateToken(String token) {

        String userName = getUsername(token);
        Optional<Users> optionalUser = userRepository.findByUsername(userName);
        Users user = optionalUser.get();

        return new UsernamePasswordAuthenticationToken(user, null, Collections.emptyList());
    }

    public String getUsername(String token) {
        return (String) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("userName");
    }

    public Authentication validateUser(Users user) {
        String userName = getUsername(user.getUsername());
        Optional<Users> optionalUser = userRepository.findByUsername(userName);
        Users item = optionalUser.get();
        if (user.getPassword().equals(item.getPassword())) {
            return new UsernamePasswordAuthenticationToken(item, null, Collections.emptyList());
        } else {
            throw new RuntimeException("Invalid password");
        }
        
    }

}
