package com.e_rental.owner.mappers;

import com.e_rental.owner.dto.request.CreateBuildingRequest;
import com.e_rental.owner.entities.BuildingEntity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BuildingMapper {
    BuildingMapper INSTANCE = Mappers.getMapper(BuildingMapper.class);

    BuildingEntity toBuildingEntity(CreateBuildingRequest createBuildingRequest);
}
