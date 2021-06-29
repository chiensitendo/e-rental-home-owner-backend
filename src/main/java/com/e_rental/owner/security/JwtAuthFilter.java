package com.e_rental.owner.security;

import com.e_rental.owner.services.UserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private final UserAuthenticationProvider userAuthenticationProvider;

    @Autowired
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(UserAuthenticationProvider userAuthenticationProvider, UserDetailsService userDetailsService) {
        this.userAuthenticationProvider = userAuthenticationProvider;
        this.userDetailsService = userDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = userAuthenticationProvider.resolveToken(httpServletRequest);

        if (token != null && userAuthenticationProvider.validateToken(token)) {
            try {
                String userName = userAuthenticationProvider.getUsername(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (RuntimeException e) {
                SecurityContextHolder.clearContext();
                throw e;
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }


}
