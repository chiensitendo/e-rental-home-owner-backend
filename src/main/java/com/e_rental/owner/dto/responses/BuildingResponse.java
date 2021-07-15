package com.e_rental.owner.dto.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BuildingResponse extends Response {
    @JsonProperty(value = "id")
    private Long id;

    @JsonProperty(value = "address")
    private String address;

    @JsonProperty(value = "cityId")
    private Integer cityId;

    @JsonProperty(value = "districtId")
    private Integer districtId;

    @JsonProperty(value = "description")
    private String description;

    @JsonProperty(value = "pictureUrl")
    private String pictureUrl;

}
