package com.e_rental.owner.dto.responses;

import com.e_rental.owner.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse extends Response {

    @JsonProperty
    Long id;

    @JsonProperty
    String token;

    @JsonProperty
    String tokenType;

    @JsonProperty
    Role role;

    @JsonProperty
    long expiredTime;

    @JsonProperty
    String userName;

    @JsonProperty
    String firstName;

    @JsonProperty
    String lastName;

    @JsonProperty
    Boolean hasInfo;

    @JsonProperty
    String email;
}
