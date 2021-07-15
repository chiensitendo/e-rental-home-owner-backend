package com.e_rental.owner.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class CreateBuildingRequest {
    @JsonProperty(value = "address")
    @Column(name = "address")
    private String address;

    @JsonProperty(value = "cityId")
    @Column(name = "city_id")
    private Integer cityId;

    @JsonProperty(value = "districtId")
    @Column(name = "district_id")
    private Integer districtId;

    @JsonProperty(value = "description")
    @Column(name = "description")
    private String description;

    @JsonProperty(value = "pictureUrl")
    @Column(name = "picture_url")
    private String pictureUrl;

}
