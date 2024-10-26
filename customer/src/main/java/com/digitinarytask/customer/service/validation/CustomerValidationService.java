package com.digitinarytask.customer.service.validation;

import com.digitinarytask.customer.domain.entity.Customer;
import com.digitinarytask.customer.domain.entity.IndividualCustomer;
import com.digitinarytask.customer.domain.entity.OrganizationCustomer;
import com.digitinarytask.customer.dto.domain.*;
import com.digitinarytask.customer.domain.enumeration.error.CustomerErrorCode;
import com.digitinarytask.customer.exception.CustomerException;
import com.digitinarytask.customer.repository.OrganizationRepository;
import com.digitinarytask.customer.service.OrganizationService;
import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerValidationService {

    private final OrganizationRepository organizationRepository;

    /**
     * Validates new customer creation
     */
    public void validateNewCustomer(CustomerDTO customerDTO) {
        Assert.notNull(customerDTO, "Customer data cannot be null");

        if (customerDTO instanceof IndividualCustomerDTO individualDTO) {
            validateIndividualCustomer(individualDTO);
        } else if (customerDTO instanceof OrganizationCustomerDTO organizationDTO) {
            validateOrganizationCustomer(organizationDTO);
        }
    }

    /**
     * Validates customer update
     */
    public void validateCustomerUpdate(Customer existingCustomer, CustomerDTO updateDTO) {
        Assert.notNull(updateDTO, "Update data cannot be null");
        Assert.notNull(existingCustomer, "Existing customer cannot be null");

        validateCustomerTypeCompatibility(existingCustomer, updateDTO);

        if (existingCustomer instanceof IndividualCustomer && updateDTO instanceof IndividualCustomerDTO) {
            validateIndividualCustomerUpdate((IndividualCustomer) existingCustomer,
                (IndividualCustomerDTO) updateDTO);
        } else if (existingCustomer instanceof OrganizationCustomer && updateDTO instanceof OrganizationCustomerDTO) {
            validateOrganizationCustomerUpdate(
                (OrganizationCustomerDTO) updateDTO);
        }
    }

    /**
     * Validates customer deletion
     */
    public void validateCustomerDeletion(Customer customer) {
        Assert.notNull(customer, "Customer cannot be null");

        if (!customer.getAccounts().isEmpty()) {
            throw new CustomerException("Cannot delete customer with active accounts",
                CustomerErrorCode.CUSTOMER_HAS_ACTIVE_ACCOUNTS);
        }
    }

    // Private validation methods
    private void validateIndividualCustomer(IndividualCustomerDTO customerDTO) {
        validateNationalId(customerDTO.getNationalId());
    }

    private void validateOrganizationCustomer(OrganizationCustomerDTO customerDTO) {
        validateOrganization(customerDTO.getOrganization());
    }

    private void validateNationalId(String nationalId) {
        if (nationalId == null || nationalId.trim().isEmpty()) {
            throw new CustomerException("National ID is required",
                CustomerErrorCode.INVALID_CUSTOMER_DATA);
        }
    }

    private void validateOrganization(OrganizationDTO organization) {
        if (organization == null) {
            throw new CustomerException("Organization is required",
                CustomerErrorCode.INVALID_CUSTOMER_DATA);
        }

        if (organization.getId() != null && !organizationRepository.existsById(organization.getId())) {
            throw new CustomerException("Organization not found",
                CustomerErrorCode.INVALID_CUSTOMER_DATA);
        }
    }

    private void validateCustomerTypeCompatibility(Customer customer, CustomerDTO customerDTO) {
        if ((customer instanceof IndividualCustomer && !(customerDTO instanceof IndividualCustomerDTO)) ||
            (customer instanceof OrganizationCustomer && !(customerDTO instanceof OrganizationCustomerDTO))) {
            throw new CustomerException("Invalid customer type",
                CustomerErrorCode.INVALID_CUSTOMER_TYPE);
        }
    }

    private void validateIndividualCustomerUpdate(IndividualCustomer existingCustomer,
                                                  IndividualCustomerDTO updateDTO) {
        if (!existingCustomer.getNationalId().equals(updateDTO.getNationalId())) {
            validateNationalId(updateDTO.getNationalId());
        }
    }

    private void validateOrganizationCustomerUpdate(OrganizationCustomerDTO updateDTO) {
        validateOrganization(updateDTO.getOrganization());
    }

    public void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new CustomerException("Invalid ID",
                CustomerErrorCode.INVALID_CUSTOMER_DATA);
        }
    }
}
