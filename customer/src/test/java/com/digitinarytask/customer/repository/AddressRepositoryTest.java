package com.digitinarytask.customer.repository;

import com.digitinarytask.customer.domain.entity.Address;
import com.digitinarytask.customer.domain.entity.Customer;
import com.digitinarytask.customer.domain.entity.IndividualCustomer;
import com.digitinarytask.customer.domain.enumeration.AddressType;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class AddressRepositoryTest {

    @Autowired
    private EntityManager entityManager;


    @Autowired
    private AddressRepository addressRepository;

    private Address address;
    private Customer customer;

    @BeforeEach
    void setUp() {
        address = Address.builder()
            .city("New York")
            .country("USA")
            .postalCode("10001")
            .state("NY")
            .addressType(AddressType.HOME)
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

        entityManager.persist(customer);
        entityManager.flush();

    }

    @Test
    void findByCustomerIdReturnsEmptyPageWhenNoAddressesMatch() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Address> addresses = addressRepository.findByCustomerId(999L, pageable);

        assertNotNull(addresses);
        assertTrue(addresses.isEmpty());
    }

    @Test
    void findByCustomerIdReturnsNonEmptyPageWhenAddressesMatch() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Address> addresses = addressRepository.findByCustomerId(customer.getId(), pageable);

        assertNotNull(addresses);
        assertFalse(addresses.isEmpty());
    }

    @Test
    void findByCustomerIdReturnsCorrectAddresses() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Address> addresses = addressRepository.findByCustomerId(customer.getId(), pageable);

        assertNotNull(addresses);
        assertFalse(addresses.isEmpty());
        assertEquals("New York", addresses.getContent().get(0).getCity());
    }

    @Test
    void findByCustomerIdReturnsEmptyPageWhenCustomerIdIsNull() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Address> addresses = addressRepository.findByCustomerId(null, pageable);

        assertNotNull(addresses);
        assertTrue(addresses.isEmpty());
    }
}
