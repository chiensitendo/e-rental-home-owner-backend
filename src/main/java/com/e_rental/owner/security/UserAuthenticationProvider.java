package com.e_rental.owner.security;

import com.e_rental.owner.dto.request.LoginRequest;
import com.e_rental.owner.entities.Owner;
import com.e_rental.owner.enums.Role;
import com.e_rental.owner.repositories.OwnerRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RequiredArgsConstructor
@Component
public class UserAuthenticationProvider {

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    private final OwnerRepository ownerRepository;


    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createToken(String userName, Role role) {
        Claims claims = Jwts.claims().setSubject(userName);
        claims.put("auth", role);

        Date now = new Date();
        Date validity = new Date(now.getTime() + SecurityConstants.EXPIRATION_TIME);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }


    public String createToken(Authentication authentication){
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        Date now = new Date();
        Date validity = new Date(now.getTime() + SecurityConstants.EXPIRATION_TIME);

        return Jwts.builder()
                .setSubject(Long.toString(userPrincipal.getId()))
                .setIssuedAt(new Date())
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

//    public Authentication validateToken(String token) {
//        String userName = getUsername(token);
//        UserDetails userDetails = userDetailsServicel.loadUserByUsername(userName);
//        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//    }

    public boolean validateToken(String token) {
        Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        return true;
    }

    public String getUsername(String token) {
        return (String) Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().get("userName");
    }

    public Authentication validateUser(LoginRequest user) {
        String userName = user.getUsername();
        Optional<Owner> optionalUser = ownerRepository.findByUsername(userName);
        Owner item = optionalUser.get();
        if (user.getPassword().equals(item.getPassword())) {
            return new UsernamePasswordAuthenticationToken(item, null, Collections.emptyList());
        } else {
            throw new RuntimeException("Invalid password");
        }

    }

}
