package com.e_rental.owner.services;

import com.e_rental.owner.entities.User;
import com.e_rental.owner.enums.Role;
import com.e_rental.owner.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private final UserRepository userRepository;

    public UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(userName);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("Login " + userName + " not found");
        }
        User userItem = null;
        try {

            userItem = user.get();
            getGrantedAuthority(userItem);

        } catch (Exception e){
            e.printStackTrace();
        }
        return new org.springframework.security.core.userdetails.User(userItem.getEmail(), "{noop}" +userItem.getPassword(),
                getGrantedAuthority(userItem));
    }

    private Collection<? extends GrantedAuthority> getGrantedAuthority(User user) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        if (user.getRole().equals(Role.ROLE_ADMIN)) {
            authorities.add(new SimpleGrantedAuthority(Role.ROLE_ADMIN.name()));
        } else if (user.getRole().equals(Role.ROLE_USER)) {
            authorities.add(new SimpleGrantedAuthority(Role.ROLE_USER.name()));
        }
        authorities.add(new SimpleGrantedAuthority(Role.ROLE_OWNER.name()));
        return authorities;
    }
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
