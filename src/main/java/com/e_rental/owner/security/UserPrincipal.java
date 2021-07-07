package com.e_rental.owner.security;

import com.e_rental.owner.entities.Owner;
import com.e_rental.owner.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPrincipal implements OAuth2User, UserDetails {

    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;

    public UserPrincipal(Long userId, String email, String password, List<GrantedAuthority> authorities) {
        this.id = userId;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserPrincipal create(Owner owner) {

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(Role.ROLE_OWNER.name()));

        return new UserPrincipal(
                owner.getId(),
                owner.getEmail(),
                owner.getPassword(),
                authorities
        );
    }

    public static UserPrincipal create(Owner owner, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(owner);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return null;
    }
}
