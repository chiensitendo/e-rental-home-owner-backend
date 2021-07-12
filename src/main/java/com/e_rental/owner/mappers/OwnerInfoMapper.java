package com.e_rental.owner.mappers;

import com.e_rental.owner.dto.request.UpdateRequest;
import com.e_rental.owner.entities.OwnerInfo;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Bean;


@Mapper(componentModel = "spring")
public interface OwnerInfoMapper {
    OwnerInfoMapper INSTANCE = Mappers.getMapper(OwnerInfoMapper.class);

    OwnerInfo toOwnerInfo(UpdateRequest updateRequest);

    void updateOwnerInfo(UpdateRequest updateRequest, @MappingTarget OwnerInfo ownerInfo);
}
