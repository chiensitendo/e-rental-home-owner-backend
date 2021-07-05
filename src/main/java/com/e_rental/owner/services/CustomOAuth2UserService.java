package com.e_rental.owner.services;

import com.e_rental.owner.entities.User;
import com.e_rental.owner.enums.AuthProvider;
import com.e_rental.owner.enums.Role;
import com.e_rental.owner.handling.OAuth2AuthenticationProcessingException;
import com.e_rental.owner.repositories.UserRepository;
import com.e_rental.owner.security.OAuth2.user.OAuth2UserInfo;
import com.e_rental.owner.security.OAuth2.user.OAuth2UserInfoFactory;
import com.e_rental.owner.security.dto.UserPrincipal;
import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                oAuth2UserRequest.getClientRegistration().getRegistrationId(),
                oAuth2User.getAttributes());
        if ((oAuth2UserInfo.getEmail()).isEmpty()) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 Provider");
        }
        Optional<User> optionalUser = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
            if (!user.getProvider().equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException("You're signed up with "
                        + user.getProvider() + " account. Please use your " + oAuth2UserRequest.getClientRegistration().getRegistrationId()
                        + " account to Login");
            }
            user = updateExistingUser(user);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }
        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = new User();
        user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        user.setEmail(oAuth2UserInfo.getEmail());
        user.setUsername(oAuth2UserInfo.getEmail());
        user.setRole(Role.ROLE_USER);
        return userRepository.save(user);
    }

    private User updateExistingUser(User user) {
        return userRepository.save(user);
    }

}
