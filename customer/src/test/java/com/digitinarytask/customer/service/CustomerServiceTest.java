package com.digitinarytask.customer.service;

import com.digitinarytask.customer.domain.entity.Customer;
import com.digitinarytask.customer.domain.enumeration.error.CustomerErrorCode;
import com.digitinarytask.customer.dto.domain.CustomerDTO;
import com.digitinarytask.customer.dto.domain.IndividualCustomerDTO;
import com.digitinarytask.customer.dto.search.CustomerSearchDTO;
import com.digitinarytask.customer.dto.mapper.CustomerMapper;
import com.digitinarytask.customer.exception.CustomerException;
import com.digitinarytask.customer.repository.CustomerRepository;
import com.digitinarytask.customer.service.validation.CustomerValidationService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerMapper customerMapper;
    @Mock
    private CustomerValidationService validator;
    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private CustomerService customerService;

    private CustomerDTO testCustomerDTO;
    private Customer testCustomer;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        testCustomerDTO = IndividualCustomerDTO.builder()
            .id(1L)
            .name("John Doe")
            .mobileNo("+1234567890")
            .build();

        testCustomer = new Customer();
        testCustomer.setId(1L);
        testCustomer.setName("John Doe");

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void createCustomer_Success() {
        when(customerMapper.toEntityWithRelations(any(CustomerDTO.class))).thenReturn(testCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(customerMapper.toDTOWithRelations(any(Customer.class))).thenReturn(testCustomerDTO);

        CustomerDTO result = customerService.createCustomer(testCustomerDTO);

        assertNotNull(result);
        assertEquals(testCustomerDTO.getName(), result.getName());
        verify(validator).validateNewCustomer(any(CustomerDTO.class));
    }

    @Test
    void createCustomer_ThrowsValidationException() {
        doThrow(new CustomerException("Invalid customer data", CustomerErrorCode.INVALID_CUSTOMER_DATA))
            .when(validator).validateNewCustomer(any(CustomerDTO.class));

        assertThrows(CustomerException.class, () -> customerService.createCustomer(testCustomerDTO));
    }

    @Test
    void createCustomer_FailsToPublishEvent() {
        when(customerMapper.toEntityWithRelations(any(CustomerDTO.class))).thenReturn(testCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(customerMapper.toDTOWithRelations(any(Customer.class))).thenReturn(testCustomerDTO);
        CustomerDTO result = customerService.createCustomer(testCustomerDTO);

        assertNotNull(result);
        assertEquals(testCustomerDTO.getName(), result.getName());
    }

    @Test
    void updateCustomer_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerMapper.toEntityWithRelations(any(CustomerDTO.class))).thenReturn(testCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(customerMapper.toDTOWithRelations(any(Customer.class))).thenReturn(testCustomerDTO);

        CustomerDTO result = customerService.updateCustomer(1L, testCustomerDTO);

        assertNotNull(result);
        assertEquals(testCustomerDTO.getName(), result.getName());
        verify(validator).validateCustomerUpdate(any(Customer.class), any(CustomerDTO.class));
    }

    @Test
    void updateCustomer_ThrowsNotFoundException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        CustomerException exception = assertThrows(CustomerException.class, () -> customerService.updateCustomer(1L, testCustomerDTO));
        assertEquals(CustomerErrorCode.CUSTOMER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void updateCustomer_FailsToPublishEvent() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerMapper.toEntityWithRelations(any(CustomerDTO.class))).thenReturn(testCustomer);
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(customerMapper.toDTOWithRelations(any(Customer.class))).thenReturn(testCustomerDTO);

        CustomerDTO result = customerService.updateCustomer(1L, testCustomerDTO);

        assertNotNull(result);
        assertEquals(testCustomerDTO.getName(), result.getName());
    }

    @Test
    void deleteCustomer_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));

        customerService.deleteCustomer(1L);

        verify(customerRepository).delete(testCustomer);
    }

    @Test
    void deleteCustomer_ThrowsNotFoundException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        CustomerException exception = assertThrows(CustomerException.class, () -> customerService.deleteCustomer(1L));
        assertEquals(CustomerErrorCode.CUSTOMER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void getCustomer_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(customerMapper.toDTOWithRelations(any(Customer.class))).thenReturn(testCustomerDTO);

        CustomerDTO result = customerService.getCustomer(1L);

        assertNotNull(result);
        assertEquals(testCustomerDTO.getName(), result.getName());
    }

    @Test
    void getCustomer_ThrowsNotFoundException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        CustomerException exception = assertThrows(CustomerException.class, () -> customerService.getCustomer(1L));
        assertEquals(CustomerErrorCode.CUSTOMER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void searchCustomers_ReturnsResults() {
        CustomerSearchDTO searchDTO = CustomerSearchDTO.builder()
            .name("John Doe")
            .size(10)
            .page(0)
            .sortBy("name")
            .sortDirection("asc")
            .build();

        Page<Customer> page = new PageImpl<>(Collections.singletonList(testCustomer), pageable, 1);
        when(customerRepository.search(searchDTO)).thenReturn(page);
        when(customerMapper.toDTOWithRelations(any(Customer.class))).thenReturn(testCustomerDTO);

        Page<CustomerDTO> result = customerService.searchCustomers(searchDTO);

        assertEquals(1, result.getTotalElements());
        assertEquals(testCustomerDTO.getName(), result.getContent().get(0).getName());
    }

    @Test
    void getAllCustomers_ReturnsPagedResults() {
        Page<Customer> page = new PageImpl<>(Collections.singletonList(testCustomer), pageable, 1);
        when(customerRepository.findAll(pageable)).thenReturn(page);
        when(customerMapper.toDTOWithRelations(any(Customer.class))).thenReturn(testCustomerDTO);

        Page<CustomerDTO> result = customerService.getAllCustomers(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(testCustomerDTO.getName(), result.getContent().get(0).getName());
    }

    @Test
    void getCustomersByType_ReturnsPagedResults() {
        Page<Customer> page = new PageImpl<>(Collections.singletonList(testCustomer), pageable, 1);
        when(customerRepository.findAllByType("INDIVIDUAL", pageable)).thenReturn(page);
        when(customerMapper.toDTOWithRelations(any(Customer.class))).thenReturn(testCustomerDTO);

        Page<CustomerDTO> result = customerService.getCustomersByType("INDIVIDUAL", pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(testCustomerDTO.getName(), result.getContent().get(0).getName());
    }

    @Test
    void getCustomersByType_ReturnsEmptyPageIfNoResults() {
        Page<Customer> page = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(customerRepository.findAllByType("INDIVIDUAL", pageable)).thenReturn(page);

        Page<CustomerDTO> result = customerService.getCustomersByType("INDIVIDUAL", pageable);

        assertEquals(0, result.getTotalElements());
    }
}
