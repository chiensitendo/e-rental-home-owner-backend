package com.e_rental.owner.services;

import com.e_rental.owner.entities.Owner;
import com.e_rental.owner.enums.Role;
import com.e_rental.owner.repositories.OwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    private final OwnerRepository ownerRepository;

    public UserDetailsService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Optional<Owner> user = ownerRepository.findByUsername(userName);
        if (!user.isPresent()) {
            throw new UsernameNotFoundException("Login " + userName + " not found");
        }
        Owner ownerItem = null;
        try {

            ownerItem = user.get();
            getGrantedAuthority(ownerItem);

        } catch (Exception e){
            e.printStackTrace();
        }
        return new org.springframework.security.core.userdetails.User(ownerItem.getEmail(), "{noop}" + ownerItem.getPassword(),
                getGrantedAuthority(ownerItem));
    }

    private Collection<? extends GrantedAuthority> getGrantedAuthority(Owner owner) {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Role.ROLE_OWNER.name()));
        return authorities;
    }
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
