package com.digitinarytask.customer.repository.custom;

import com.digitinarytask.customer.domain.entity.Organization;
import com.digitinarytask.customer.dto.search.OrganizationSearchDTO;
import org.springframework.data.domain.Page;

import java.util.Optional;

/**
 * Custom repository interface for Organization entity.
 */
public interface OrganizationRepositoryCustom {
    /**
     * Searches for organizations based on search criteria.
     */
    Page<Organization> searchOrganizations(OrganizationSearchDTO searchDTO);

    /**
     * Finds organizations by type.
     */
    Optional<Organization> findByCustomerId(Long id);
}
