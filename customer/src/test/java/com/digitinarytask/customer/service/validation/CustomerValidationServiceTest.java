package com.digitinarytask.customer.service.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.digitinarytask.customer.domain.entity.Customer;
import com.digitinarytask.customer.domain.entity.IndividualCustomer;
import com.digitinarytask.customer.domain.entity.OrganizationCustomer;
import com.digitinarytask.customer.domain.enumeration.AccountStatus;
import com.digitinarytask.customer.dto.domain.CustomerDTO;
import com.digitinarytask.customer.dto.domain.IndividualCustomerDTO;
import com.digitinarytask.customer.dto.domain.OrganizationCustomerDTO;
import com.digitinarytask.customer.domain.enumeration.error.CustomerErrorCode;
import com.digitinarytask.customer.exception.CustomerException;
import com.digitinarytask.customer.repository.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerValidationServiceTest {

    @InjectMocks
    private CustomerValidationService customerValidationService;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private CustomerDTO customerDTO;

    @Mock
    private IndividualCustomerDTO individualCustomerDTO;

    @Mock
    private OrganizationCustomerDTO organizationCustomerDTO;

    @Mock
    private Customer existingCustomer;

    @Mock
    private IndividualCustomer individualCustomer;

    @Mock
    private OrganizationCustomer organizationCustomer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateId_validId_doesNotThrowException() {
        assertDoesNotThrow(() -> customerValidationService.validateId(1L));
    }

    @Test
    void validateId_invalidId_throwsCustomerException() {
        CustomerException exception = assertThrows(CustomerException.class,
            () -> customerValidationService.validateId(null));
        assertEquals(CustomerErrorCode.INVALID_CUSTOMER_DATA, exception.getErrorCode());
    }


    @Test
    void validateNewCustomer_nullCustomer_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
            () -> customerValidationService.validateNewCustomer(null));
        assertEquals("Customer data cannot be null", exception.getMessage());
    }



}
