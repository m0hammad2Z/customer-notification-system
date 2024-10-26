package com.digitinarytask.customer.repository;

import com.digitinarytask.customer.domain.entity.Customer;
import com.digitinarytask.customer.domain.entity.Organization;
import com.digitinarytask.customer.domain.entity.OrganizationCustomer;
import com.digitinarytask.customer.dto.search.OrganizationSearchDTO;
import com.digitinarytask.customer.repository.impl.OrganizationRepositoryImpl;
import com.digitinarytask.customer.repository.specification.OrganizationSpecifications;
import com.digitinarytask.customer.service.security.JwtService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class OrganizationRepositoryTest {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private OrganizationRepository organizationRepository;


    private Organization organization;
    private Customer customer;

    @BeforeEach
    void setUp() {
        organization = Organization.builder()
            .legalName("Tech Corp")
            .registrationNumber("AS22123456")
            .trademarkName("Tech")
            .build();


        customer = OrganizationCustomer.builder()
            .name("John Doe")
            .mobileNo("1234567890")
            .organization(organization)
            .build();

        entityManager.persist(customer);
        entityManager.flush();
    }

    @Test
    void searchOrganizationsReturnsEmptyPageWhenNoOrganizationsMatch() {
        OrganizationSearchDTO searchDTO = new OrganizationSearchDTO();
        searchDTO.setPage(10);
        searchDTO.setSize(10);
        searchDTO.setSortBy("legalName");
        searchDTO.setSortDirection("ASC");

        Page<Organization> organizations = organizationRepository.searchOrganizations(searchDTO);

        assertNotNull(organizations);
        assertTrue(organizations.isEmpty());
    }

    @Test
    void searchOrganizationsReturnsNonEmptyPageWhenOrganizationsMatch() {
        OrganizationSearchDTO searchDTO = new OrganizationSearchDTO();
        searchDTO.setPage(0);
        searchDTO.setSize(10);
        searchDTO.setSortBy("legalName");
        searchDTO.setSortDirection("ASC");

        Page<Organization> organizations = organizationRepository.searchOrganizations(searchDTO);

        assertNotNull(organizations);
        assertFalse(organizations.isEmpty());
    }

    @Test
    void searchOrganizationsReturnsSortedResults() {
        OrganizationSearchDTO searchDTO = new OrganizationSearchDTO();
        searchDTO.setPage(0);
        searchDTO.setSize(10);
        searchDTO.setSortBy("legalName");
        searchDTO.setSortDirection("DESC");

        Page<Organization> organizations = organizationRepository.searchOrganizations(searchDTO);

        assertNotNull(organizations);
        assertFalse(organizations.isEmpty());
        assertEquals("Tech Corp", organizations.getContent().get(0).getLegalName());
    }

    @Test
    void findByCustomerIdReturnsEmptyWhenIdDoesNotExist() {
        Optional<Organization> foundOrganization = organizationRepository.findByCustomerId(999L);

        assertFalse(foundOrganization.isPresent());
    }

    @Test
    void findByCustomerIdReturnsCorrectOrganization() {
        Optional<Organization> foundOrganization = organizationRepository.findByCustomerId(customer.getId());

        assertTrue(foundOrganization.isPresent());
        assertEquals("Tech Corp", foundOrganization.get().getLegalName());
    }

    @Test
    void findByCustomerIdReturnsEmptyWhenCustomerIdIsNull() {
        Optional<Organization> foundOrganization = organizationRepository.findByCustomerId(null);

        assertFalse(foundOrganization.isPresent());
    }
}
