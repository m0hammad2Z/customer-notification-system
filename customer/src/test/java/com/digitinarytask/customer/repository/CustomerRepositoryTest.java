package com.digitinarytask.customer.repository;

import com.digitinarytask.customer.domain.entity.*;
import com.digitinarytask.customer.domain.enumeration.AccountStatus;
import com.digitinarytask.customer.domain.enumeration.AccountType;
import com.digitinarytask.customer.domain.enumeration.AddressType;
import com.digitinarytask.customer.dto.search.CustomerSearchDTO;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    private OrganizationCustomer orgCustomer;
    private IndividualCustomer customer;
    private Organization organization;
    private Address address;
    private Account account;

    @BeforeEach
    void setUp() {
        // First create the organization
        organization = Organization.builder()
            .legalName("Tech Corp")
            .registrationNumber("AS22123456")
            .trademarkName("Tech")
            .build();

        address = Address.builder()
            .city("New York")
            .country("USA")
            .postalCode("10001")
            .state("NY")
            .addressType(AddressType.HOME)
            .build();

        account = Account.builder()
            .accountNumber("123456789")
            .accountName("John Doe")
            .status(AccountStatus.ACTIVE)
            .accountType(AccountType.CURRENT)
            .balance(BigDecimal.valueOf(1000))
            .build();

        // Create customers and set their addresses
        orgCustomer = OrganizationCustomer.builder()
            .name("John Doe")
            .organization(organization)
            .addresses(Collections.singleton(address))
            .build();

        customer = IndividualCustomer.builder()
            .name("John Doe")
            .firstName("John")
            .lastName("Doe")
            .mobileNo("1234567890")
            .nationalId("123456789")
            .addresses(Collections.singleton(address))
            .build();

        address.setCustomer(customer);
        account.setCustomer(customer);

        entityManager.persist(organization);
        entityManager.persist(customer);
        entityManager.persist(orgCustomer);
        entityManager.persist(address);
        entityManager.persist(account);

        entityManager.flush();
    }

    @Test
    void searchReturnsEmptyPageWhenNoCustomersMatch() {
        CustomerSearchDTO searchDTO = new CustomerSearchDTO();
        searchDTO.setPage(0);
        searchDTO.setSize(10);
        searchDTO.setSortBy("name");
        searchDTO.setSortDirection("ASC");
        searchDTO.setName("nonexistentName");

        Page<Customer> customers = customerRepository.search(searchDTO);

        assertNotNull(customers);
        assertTrue(customers.isEmpty());
    }

    @Test
    void searchReturnsNonEmptyPageWhenCustomersMatch() {
        CustomerSearchDTO searchDTO = new CustomerSearchDTO();
        searchDTO.setPage(0);
        searchDTO.setSize(10);

        Page<Customer> customers = customerRepository.search(searchDTO);

        assertNotNull(customers);
        assertFalse(customers.isEmpty());
    }

    @Test
    void findAllByTypeReturnsEmptyPageWhenNoCustomersMatch() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Customer> customers = customerRepository.findAllByType("nonexistentType", pageable);

        assertNotNull(customers);
        assertTrue(customers.isEmpty());
    }

    @Test
    void findAllByTypeReturnsNonEmptyPageWhenCustomersMatch() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Customer> customers = customerRepository.findAllByType("ORGANIZATION", pageable);

        assertNotNull(customers);
        assertFalse(customers.isEmpty());
    }

    @Test
    void existsByNationalIdReturnsTrueWhenCustomerExists() {
        boolean exists = customerRepository.existsByNationalId("123456789");

        assertTrue(exists);
    }

    @Test
    void existsByNationalIdReturnsFalseWhenCustomerDoesNotExist() {
        boolean exists = customerRepository.existsByNationalId("nonexistentNationalId");

        assertFalse(exists);
    }

}
