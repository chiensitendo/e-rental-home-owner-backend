package com.e_rental.owner.mappers;

import com.e_rental.owner.dto.request.UpdateOwnerRequest;
import com.e_rental.owner.dto.responses.OwnerInfoResponse;
import com.e_rental.owner.entities.OwnerInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface OwnerInfoMapper {
    OwnerInfoMapper INSTANCE = Mappers.getMapper(OwnerInfoMapper.class);

    OwnerInfo toOwnerInfo(UpdateOwnerRequest updateOwnerRequest);

    void updateOwnerInfo(UpdateOwnerRequest updateOwnerRequest, @MappingTarget OwnerInfo ownerInfo);
    @Mapping(target="ownerId", source="info.owner.id")
    OwnerInfoResponse toOwnerInfoResponse(OwnerInfo info);
}
