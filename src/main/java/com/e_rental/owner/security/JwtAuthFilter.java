package com.e_rental.owner.security;

import com.e_rental.owner.entities.Users;
import com.e_rental.owner.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private final UserAuthenticationProvider userAuthenticationProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = userAuthenticationProvider.resolveToken(httpServletRequest);

        if (token != null && userAuthenticationProvider.validateToken(token)) {
            try {
                Optional<Users> optionalUser = userRepository.findByUsername(userAuthenticationProvider.getUsername(token));
                Users user = optionalUser.get();
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        user, null, Collections.emptyList());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (RuntimeException e) {
                SecurityContextHolder.clearContext();
                throw e;
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }


}
