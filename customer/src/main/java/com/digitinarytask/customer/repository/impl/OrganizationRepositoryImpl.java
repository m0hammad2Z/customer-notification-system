package com.digitinarytask.customer.repository.impl;

import com.digitinarytask.customer.domain.entity.Organization;
import com.digitinarytask.customer.dto.search.OrganizationSearchDTO;
import com.digitinarytask.customer.repository.AbstractSpecificationRepository;
import com.digitinarytask.customer.repository.custom.OrganizationRepositoryCustom;
import com.digitinarytask.customer.repository.specification.OrganizationSpecifications;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Optional;

public class OrganizationRepositoryImpl extends AbstractSpecificationRepository<Organization>
    implements OrganizationRepositoryCustom {

    public OrganizationRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }

    /**
     * Searches for organizations based on search criteria.
     */
    @Override
    public Page<Organization> searchOrganizations(OrganizationSearchDTO searchDTO) {
        Sort sort = searchDTO.getSortBy() != null
            ? Sort.by(Sort.Direction.fromString(searchDTO.getSortDirection()), searchDTO.getSortBy())
            : Sort.by(Sort.Direction.ASC, "legalName");

        return findBySpecification(
            OrganizationSpecifications.withSearchCriteria(searchDTO),
            PageRequest.of(searchDTO.getPage(), searchDTO.getSize(), sort),
            Organization.class
        );
    }

    /**
     * Finds organizations by customer ID.
     */
    @Override
    public Optional<Organization> findByCustomerId(Long id) {
        return findFirst(OrganizationSpecifications.withCustomerId(id), Organization.class);
    }


}
