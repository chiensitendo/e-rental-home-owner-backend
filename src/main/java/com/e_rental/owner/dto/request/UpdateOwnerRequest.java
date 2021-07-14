package com.e_rental.owner.dto.request;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.Column;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UpdateOwnerRequest {
    @JsonProperty(value = "firstName")
    @Column(name = "first_name")
    private String firstName;

    @JsonProperty(value = "lastName")
    @Column(name = "last_name")
    private String lastName;

    @JsonProperty(value = "gender")
    @Column(name = "gender")
    private Integer gender;

    @JsonProperty(value = "provinceId")
    @Column(name = "province_id")
    private Integer provinceId;

    @JsonProperty(value = "address")
    @Column(name = "address")
    private String address;
}
