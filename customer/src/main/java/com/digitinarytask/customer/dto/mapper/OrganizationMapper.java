package com.digitinarytask.customer.dto.mapper;

import com.digitinarytask.customer.domain.entity.Organization;
import com.digitinarytask.customer.dto.domain.OrganizationDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {
    OrganizationMapper INSTANCE = Mappers.getMapper(OrganizationMapper.class);

    OrganizationDTO toDTO(Organization customerOrganization);
    Organization toEntity(OrganizationDTO customerOrganizationDTO);

}
