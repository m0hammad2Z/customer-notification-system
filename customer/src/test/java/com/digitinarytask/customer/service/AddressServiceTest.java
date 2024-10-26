package com.digitinarytask.customer.service;

import com.digitinarytask.customer.domain.entity.Address;
import com.digitinarytask.customer.domain.entity.Customer;
import com.digitinarytask.customer.domain.enumeration.error.AddressErrorCode;
import com.digitinarytask.customer.domain.enumeration.error.CustomerErrorCode;
import com.digitinarytask.customer.dto.domain.AddressDTO;
import com.digitinarytask.customer.exception.AddressException;
import com.digitinarytask.customer.exception.CustomerException;
import com.digitinarytask.customer.repository.AddressRepository;
import com.digitinarytask.customer.repository.CustomerRepository;
import com.digitinarytask.customer.dto.mapper.AddressMapper;
import com.digitinarytask.customer.service.validation.AddressValidationService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @Mock
    private AddressRepository addressRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private AddressMapper addressMapper;
    @Mock
    private AddressValidationService addressValidationService;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private AddressService addressService;

    private AddressDTO testAddressDTO;
    private Address testAddress;
    private Customer testCustomer;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);

        testAddressDTO = AddressDTO.builder()
            .id(1L)
            .postalCode("12345")
            .city("Sample City")
            .state("Sample State")
            .country("Sample Country")
            .build();

        testAddress = new Address();
        testAddress.setId(1L);
        testAddress.setPostalCode("12345");
        testAddress.setCity("Sample City");
        testAddress.setCustomer(testCustomer);


        pageable = PageRequest.of(0, 10);
    }

    @Test
    void findCustomerAddresses_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(addressRepository.findByCustomerId(1L, pageable)).thenReturn(new PageImpl<>(Collections.singletonList(testAddress)));
        when(addressMapper.toDTO(testAddress)).thenReturn(testAddressDTO);

        Page<AddressDTO> result = addressService.findCustomerAddresses(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testAddressDTO.getPostalCode(), result.getContent().get(0).getPostalCode());
    }

    @Test
    void findCustomerAddresses_CustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        CustomerException exception = assertThrows(CustomerException.class, () -> addressService.findCustomerAddresses(1L, pageable));
        assertEquals(CustomerErrorCode.CUSTOMER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void findCustomerAddresses_ErrorRetrievingAddresses() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(addressRepository.findByCustomerId(1L, pageable)).thenThrow(new RuntimeException("Database error"));

        AddressException exception = assertThrows(AddressException.class, () -> addressService.findCustomerAddresses(1L, pageable));
        assertEquals(AddressErrorCode.ADDRESS_RETRIEVAL_ERROR, exception.getErrorCode());
    }

    @Test
    void addCustomerAddress_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        doNothing().when(addressValidationService).validateAddressDTO(testAddressDTO);
        when(addressMapper.toEntity(testAddressDTO)).thenReturn(testAddress);
        when(addressRepository.save(testAddress)).thenReturn(testAddress);
        when(addressMapper.toDTO(testAddress)).thenReturn(testAddressDTO);

        AddressDTO result = addressService.addCustomerAddress(1L, testAddressDTO);

        assertNotNull(result);
        assertEquals(testAddressDTO.getPostalCode(), result.getPostalCode());
    }

    @Test
    void addCustomerAddress_CustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        CustomerException exception = assertThrows(CustomerException.class, () -> addressService.addCustomerAddress(1L, testAddressDTO));
        assertEquals(CustomerErrorCode.CUSTOMER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void addCustomerAddress_ValidationError() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        doThrow(new AddressException("Invalid address data", AddressErrorCode.ADDRESS_INVALID))
            .when(addressValidationService).validateAddressDTO(any(AddressDTO.class));

        AddressException exception = assertThrows(AddressException.class, () -> addressService.addCustomerAddress(1L, testAddressDTO));
        assertEquals("Invalid address data", exception.getMessage());
    }

    @Test
    void addCustomerAddress_ErrorSavingAddress() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        doNothing().when(addressValidationService).validateAddressDTO(testAddressDTO);
        when(addressMapper.toEntity(testAddressDTO)).thenReturn(testAddress);
        when(addressRepository.save(testAddress)).thenThrow(new RuntimeException("Database error"));

        AddressException exception = assertThrows(AddressException.class, () -> addressService.addCustomerAddress(1L, testAddressDTO));
        assertEquals(AddressErrorCode.ADDRESS_CREATION_ERROR, exception.getErrorCode());
    }

    @Test
    void updateCustomerAddress_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));
        doNothing().when(addressValidationService).validateAddressDTO(testAddressDTO);
        when(addressMapper.toEntity(testAddressDTO)).thenReturn(testAddress);
        when(addressRepository.save(testAddress)).thenReturn(testAddress);
        when(addressMapper.toDTO(testAddress)).thenReturn(testAddressDTO);

        AddressDTO result = addressService.updateCustomerAddress(1L, 1L, testAddressDTO);

        assertNotNull(result);
        assertEquals(testAddressDTO.getPostalCode(), result.getPostalCode());
    }

    @Test
    void updateCustomerAddress_CustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        CustomerException exception = assertThrows(CustomerException.class, () -> addressService.updateCustomerAddress(1L, 1L, testAddressDTO));
        assertEquals(CustomerErrorCode.CUSTOMER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void updateCustomerAddress_AddressNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(addressRepository.findById(1L)).thenReturn(Optional.empty());

        AddressException exception = assertThrows(AddressException.class, () -> addressService.updateCustomerAddress(1L, 1L, testAddressDTO));
        assertEquals(AddressErrorCode.ADDRESS_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void updateCustomerAddress_ErrorSavingAddress() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));
        doNothing().when(addressValidationService).validateAddressDTO(testAddressDTO);
        when(addressMapper.toEntity(testAddressDTO)).thenReturn(testAddress);
        when(addressRepository.save(testAddress)).thenThrow(new RuntimeException("Database error"));

        AddressException exception = assertThrows(AddressException.class, () -> addressService.updateCustomerAddress(1L, 1L, testAddressDTO));
        assertEquals(AddressErrorCode.ADDRESS_UPDATE_ERROR, exception.getErrorCode());
    }

    @Test
    void deleteCustomerAddress_Success() {
        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));

        addressService.deleteCustomerAddress(1L);

        verify(addressRepository).findById(1L);
        verify(addressRepository).deleteById(1L);
    }

    @Test
    void deleteCustomerAddress_ErrorDeletingAddress() {

        when(addressRepository.findById(1L)).thenReturn(Optional.of(testAddress));
        doThrow(new RuntimeException("Database error"))
            .when(addressRepository)
            .deleteById(1L);

        AddressException exception = assertThrows(AddressException.class,
            () -> addressService.deleteCustomerAddress(1L));
        assertEquals(AddressErrorCode.ADDRESS_DELETION_ERROR, exception.getErrorCode());
    }
}
