package com.digitinarytask.customer.service.validation;

import com.digitinarytask.customer.domain.entity.Organization;
import com.digitinarytask.customer.domain.enumeration.AccountStatus;
import com.digitinarytask.customer.domain.enumeration.error.AddressErrorCode;
import com.digitinarytask.customer.dto.domain.OrganizationDTO;
import com.digitinarytask.customer.domain.enumeration.error.OrganizationErrorCode;
import com.digitinarytask.customer.exception.AddressException;
import com.digitinarytask.customer.exception.OrganizationException;
import com.digitinarytask.customer.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


@Service
public class OrganizationValidationService {

    /**
     * Validates ID format and rules.
     */
    public void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new OrganizationException("Invalid organization ID",
                OrganizationErrorCode.INVALID_ORG_ID);
        }
    }


    /**
     * Validates organization update
     */
    public void validateOrganizationUpdate(Organization existingOrganization, OrganizationDTO updateDTO) {
        Assert.notNull(updateDTO, "Update data cannot be null");
        Assert.notNull(existingOrganization, "Existing organization cannot be null");

        validateOrganizationName(updateDTO.getLegalName());
        validateRegistrationNumber(updateDTO.getRegistrationNumber());

        // Additional validation for updates
        if (!existingOrganization.getRegistrationNumber().equals(updateDTO.getRegistrationNumber())) {
            throw new OrganizationException("Registration number cannot be changed",
                OrganizationErrorCode.INVALID_UPDATE);
        }
    }

    // Private validation methods
    private void validateOrganizationName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new OrganizationException("Organization name is required",
                OrganizationErrorCode.INVALID_ORG_DATA);
        }

        if (name.length() < 2 || name.length() > 100) {
            throw new OrganizationException("Organization name must be between 2 and 100 characters",
                OrganizationErrorCode.INVALID_ORG_DATA);
        }
    }

    private void validateRegistrationNumber(String registrationNumber) {
        if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
            throw new OrganizationException("Invalid registration number format",
                OrganizationErrorCode.INVALID_ORG_DATA);
        }
    }
}

