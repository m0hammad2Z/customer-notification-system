package com.digitinarytask.customer.service;

import com.digitinarytask.customer.domain.entity.Organization;
import com.digitinarytask.customer.dto.domain.OrganizationDTO;
import com.digitinarytask.customer.dto.mapper.OrganizationMapper;
import com.digitinarytask.customer.dto.search.OrganizationSearchDTO;
import com.digitinarytask.customer.domain.enumeration.error.OrganizationErrorCode;
import com.digitinarytask.customer.exception.OrganizationException;
import com.digitinarytask.customer.repository.CustomerRepository;
import com.digitinarytask.customer.repository.OrganizationRepository;
import com.digitinarytask.customer.service.validation.OrganizationValidationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;
    private final OrganizationValidationService organizationValidationService;


    /**
     * Creates a new organization.
     */
    @Transactional
    @Caching(
        put = {
            @CachePut(value = "organizations", key = "#result.id"),
            @CachePut(value = "organizationsByCustomer", key = "#customerId")
        },
        evict = @CacheEvict(value = "customers", key = "#customerId")
    )
    public OrganizationDTO updateOrganization(Long customerId, @Valid OrganizationDTO organizationDTO) {
        log.info("Updating organization: {}", organizationDTO);

        try {

            // Fetch organization
            Organization organization = getOrganizationByCustomerIdOrThrow(customerId);
            organizationValidationService.validateId(customerId);
            organizationValidationService.validateOrganizationUpdate(organization, organizationDTO);

            // Update organization
            organizationDTO.setId(organization.getId());
            organization = organizationMapper.toEntity(organizationDTO);
            organization = organizationRepository.save(organization);

            log.info("Organization updated successfully with ID: {}", organization.getId());
            return organizationMapper.toDTO(organization);

        } catch (OrganizationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating organization: {}", e.getMessage(), e);
            throw new OrganizationException("Failed to update organization",
                OrganizationErrorCode.UPDATE_ORG_FAILED);
        }
    }


    /**
     * Creates a new organization.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "organizationsByCustomer", key = "#customerId")
    public OrganizationDTO getOrganization(Long customerId) {
        log.debug("Fetching organization: {}", customerId);
        organizationValidationService.validateId(customerId);
        return organizationMapper.toDTO(getOrganizationByCustomerIdOrThrow(customerId));
    }

    /**
     * Searches organizations based on search criteria.
     */
    @Transactional(readOnly = true)
    public Page<OrganizationDTO> searchOrganizations(@Valid OrganizationSearchDTO searchDTO) {
        log.debug("Searching organizations with criteria: {}", searchDTO);
        try {
            return organizationRepository.searchOrganizations(searchDTO)
                .map(organizationMapper::toDTO);
        } catch (Exception e) {
            log.error("Error searching organizations: {}", e.getMessage(), e);
            throw new OrganizationException("Failed to search organizations",
                OrganizationErrorCode.SEARCH_ORG_FAILED);
        }
    }

    /**
     * Fetches organizations with pagination.
     */
    @Transactional(readOnly = true)
    public Page<OrganizationDTO> getOrganizations(Pageable pageable) {
        log.debug("Fetching organizations with pagination: {}", pageable);
        try {
            return organizationRepository.findAll(pageable)
                .map(organizationMapper::toDTO);
        } catch (Exception e) {
            log.error("Error fetching organizations: {}", e.getMessage(), e);
            throw new OrganizationException("Failed to fetch organizations",
                OrganizationErrorCode.FETCH_ORG_FAILED);
        }
    }


    // Helper methods

    /**
     * Fetches an organization by customer ID or throws an exception if not found.
     */
    private Organization getOrganizationByCustomerIdOrThrow(Long id) {
        organizationValidationService.validateId(id);
        return organizationRepository.findByCustomerId(id)
            .orElseThrow(() -> new OrganizationException(
                "Organization not found with id: " + id,
                OrganizationErrorCode.ORG_NOT_FOUND
            ));
    }

}
