package com.digitinarytask.customer.dto.mapper;

import com.digitinarytask.customer.domain.entity.Customer;
import com.digitinarytask.customer.domain.entity.IndividualCustomer;
import com.digitinarytask.customer.domain.entity.OrganizationCustomer;
import com.digitinarytask.customer.domain.enumeration.error.CustomerErrorCode;
import com.digitinarytask.customer.dto.domain.CustomerDTO;
import com.digitinarytask.customer.dto.domain.IndividualCustomerDTO;
import com.digitinarytask.customer.dto.domain.OrganizationCustomerDTO;
import com.digitinarytask.customer.exception.CustomerException;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {AddressMapper.class, AccountMapper.class, OrganizationMapper.class})
public interface CustomerMapper {

    @Named("individualCustomerToDTOWithRelations")
    @Mapping(target = "accounts", source = "accounts")
    @Mapping(target = "addresses", source = "addresses")
    IndividualCustomerDTO individualCustomerToDTOWithRelations(IndividualCustomer customer);

    @InheritInverseConfiguration(name = "individualCustomerToDTOWithRelations")
    IndividualCustomer individualCustomerDTOToEntityWithRelations(IndividualCustomerDTO dto);

    @Named("organizationCustomerToDTOWithRelations")
    OrganizationCustomerDTO organizationCustomerToDTOWithRelations(OrganizationCustomer customer);

    @Named("organizationCustomerDTOToEntityWithRelations")
    @InheritInverseConfiguration(name = "organizationCustomerToDTOWithRelations")
    OrganizationCustomer organizationCustomerDTOToEntityWithRelations(OrganizationCustomerDTO dto);


    @Named("toDTOWithRelations")
    default CustomerDTO toDTOWithRelations(Customer customer) {
        if (customer instanceof IndividualCustomer) {
            return individualCustomerToDTOWithRelations((IndividualCustomer) customer);
        } else if (customer instanceof OrganizationCustomer) {
            return organizationCustomerToDTOWithRelations((OrganizationCustomer) customer);
        }
        throw new CustomerException("Unknown customer DTO type, supported only INDIVIDUAL and ORGANIZATION", CustomerErrorCode.CREATE_CUSTOMER_FAILED);
    }

    @Named("toEntityWithRelations")
    default Customer toEntityWithRelations(CustomerDTO dto) {
        if (dto instanceof IndividualCustomerDTO)  {
            return individualCustomerDTOToEntityWithRelations((IndividualCustomerDTO) dto);
        } else if (dto instanceof OrganizationCustomerDTO) {
            return organizationCustomerDTOToEntityWithRelations((OrganizationCustomerDTO) dto);
        }
        throw new CustomerException("Unknown customer DTO type, supported only INDIVIDUAL and ORGANIZATION", CustomerErrorCode.CREATE_CUSTOMER_FAILED);
    }
}
