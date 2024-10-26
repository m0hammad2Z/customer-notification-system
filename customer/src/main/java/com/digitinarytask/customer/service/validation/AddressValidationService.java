package com.digitinarytask.customer.service.validation;

import com.digitinarytask.customer.domain.enumeration.error.AddressErrorCode;
import com.digitinarytask.customer.dto.domain.AddressDTO;
import com.digitinarytask.customer.exception.AddressException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


@Service
public class AddressValidationService {

    /**
     * Validates the complete address DTO against all business rules.
     */
    public void validateAddressDTO(AddressDTO address) {
        Assert.notNull(address, "Address cannot be null");


        validatePostalCode(address.getPostalCode());
        validateCity(address.getCity());
        validateCountry(address.getCountry());
    }

    /**
     * Validates postal code format and rules.
     */
    private void validatePostalCode(String postalCode) {
        if (postalCode == null || postalCode.trim().isEmpty()) {
            throw new AddressException("Postal code is required",
                AddressErrorCode.ADDRESS_INVALID);
        }
    }


    /**
     * Validates city name format and rules.
     */
    private void validateCity(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new AddressException("City is required",
                AddressErrorCode.ADDRESS_INVALID);
        }
    }


    /**
     * Validates country format and rules.
     */
    private void validateCountry(String country) {
        if (country == null || country.trim().isEmpty()) {
            throw new AddressException("Country is required",
                AddressErrorCode.ADDRESS_INVALID);
        }
    }


    /**
     * Validates ID format and rules.
     */
    public void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new AddressException("Invalid address ID",
                AddressErrorCode.ADDRESS_INVALID);
        }
    }

}
