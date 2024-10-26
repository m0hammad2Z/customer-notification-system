package com.digitinarytask.customer.dto.mapper;

import com.digitinarytask.customer.domain.entity.Address;
import com.digitinarytask.customer.dto.domain.AddressDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    @Named("toDTO")
    AddressDTO toDTO(Address address);

    @Named("toEntity")
    Address toEntity(AddressDTO addressDTO);

}
