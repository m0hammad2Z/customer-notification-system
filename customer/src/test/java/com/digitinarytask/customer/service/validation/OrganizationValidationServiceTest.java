package com.digitinarytask.customer.service.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.digitinarytask.customer.domain.entity.Organization;
import com.digitinarytask.customer.dto.domain.OrganizationDTO;
import com.digitinarytask.customer.domain.enumeration.error.OrganizationErrorCode;
import com.digitinarytask.customer.exception.OrganizationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrganizationValidationServiceTest {

    @InjectMocks
    private OrganizationValidationService organizationValidationService;

    @Mock
    private Organization existingOrganization;

    @Mock
    private OrganizationDTO updateDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateId_validId_doesNotThrowException() {
        assertDoesNotThrow(() -> organizationValidationService.validateId(1L));
    }

    @Test
    void validateId_invalidId_throwsOrganizationException() {
        OrganizationException exception = assertThrows(OrganizationException.class,
            () -> organizationValidationService.validateId(null));
        assertEquals(OrganizationErrorCode.INVALID_ORG_ID, exception.getErrorCode());
    }

    @Test
    void validateOrganizationUpdate_validUpdate_doesNotThrowException() {
        when(updateDTO.getLegalName()).thenReturn("Valid Organization Name");
        when(updateDTO.getRegistrationNumber()).thenReturn("123456");
        when(existingOrganization.getRegistrationNumber()).thenReturn("123456");

        assertDoesNotThrow(() -> organizationValidationService.validateOrganizationUpdate(existingOrganization, updateDTO));
    }


    @Test
    void validateOrganizationUpdate_nullUpdateDTO_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> organizationValidationService.validateOrganizationUpdate(existingOrganization, null));
        assertEquals("Update data cannot be null", exception.getMessage());
    }
}
