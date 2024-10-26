package com.digitinarytask.customer.service.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.digitinarytask.customer.domain.enumeration.error.AddressErrorCode;
import com.digitinarytask.customer.dto.domain.AddressDTO;
import com.digitinarytask.customer.exception.AddressException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddressValidationServiceTest {

    @InjectMocks
    private AddressValidationService addressValidationService;

    @Mock
    private AddressDTO addressDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateId_validId_doesNotThrowException() {
        assertDoesNotThrow(() -> addressValidationService.validateId(1L));
    }

    @Test
    void validateId_nullId_throwsAddressException() {
        AddressException exception = assertThrows(AddressException.class,
            () -> addressValidationService.validateId(null));
        assertEquals(AddressErrorCode.ADDRESS_INVALID, exception.getErrorCode());
    }

    @Test
    void validateId_nonPositiveId_throwsAddressException() {
        AddressException exception = assertThrows(AddressException.class,
            () -> addressValidationService.validateId(-1L));
        assertEquals(AddressErrorCode.ADDRESS_INVALID, exception.getErrorCode());
    }

    @Test
    void validateAddressDTO_validAddress_doesNotThrowException() {
        when(addressDTO.getPostalCode()).thenReturn("12345");
        when(addressDTO.getCity()).thenReturn("CityName");
        when(addressDTO.getCountry()).thenReturn("CountryName");

        assertDoesNotThrow(() -> addressValidationService.validateAddressDTO(addressDTO));
    }

    @Test
    void validateAddressDTO_nullAddress_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> addressValidationService.validateAddressDTO(null));
        assertEquals("Address cannot be null", exception.getMessage());
    }


    @Test
    void validateAddressDTO_emptyCountry_throwsAddressException() {
        when(addressDTO.getPostalCode()).thenReturn("12345");
        when(addressDTO.getCity()).thenReturn("CityName");
        when(addressDTO.getCountry()).thenReturn("");

        AddressException exception = assertThrows(AddressException.class,
            () -> addressValidationService.validateAddressDTO(addressDTO));
        assertEquals(AddressErrorCode.ADDRESS_INVALID, exception.getErrorCode());
    }
}
