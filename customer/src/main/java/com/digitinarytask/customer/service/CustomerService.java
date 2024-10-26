package com.digitinarytask.customer.service;

import com.digitinarytask.shared.annotation.Notifiable;
import com.digitinarytask.customer.domain.entity.*;
import com.digitinarytask.customer.dto.domain.CustomerDTO;
import com.digitinarytask.customer.dto.mapper.CustomerMapper;
import com.digitinarytask.customer.dto.search.CustomerSearchDTO;
import com.digitinarytask.customer.domain.enumeration.error.CustomerErrorCode;
import com.digitinarytask.customer.exception.CustomerException;
import com.digitinarytask.customer.repository.CustomerRepository;
import com.digitinarytask.customer.service.validation.CustomerValidationService;
import com.digitinarytask.shared.enumeration.NotificationType;
import jakarta.persistence.EntityManager;
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
import jakarta.validation.Valid;

/**
 * Service for managing customers.
 */
@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;
    private final CustomerValidationService validator;
    private final EntityManager entityManager;


    /**
     * Creates a new customer.
     */
    @Transactional
    @Caching(
        evict = {
            @CacheEvict(value = "customers", key = "#result.id"),
            @CacheEvict(value = "organizationsByCustomer", key = "#result.id", condition = "#result.organization != null")
        }
    )
    @Notifiable(type = NotificationType.CUSTOMER_CREATED, entityType = "CUSTOMER")
    public CustomerDTO createCustomer(@Valid CustomerDTO customerDTO) {
        log.info("Creating new customer of type: {}", customerDTO.getClass().getSimpleName());

        try {
            // Validate based on customer type
            validator.validateNewCustomer(customerDTO);

            // Map and prepare customer entity
            Customer customer = prepareCustomerEntity(customerDTO);

            // Save customer
            customer = customerRepository.save(customer);


            log.info("Customer created successfully with ID: {}", customer.getId());
            return customerMapper.toDTOWithRelations(customer);

        } catch (CustomerException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating customer: {}", e.getMessage(), e);
            throw new CustomerException("Failed to create customer",
                CustomerErrorCode.CREATE_CUSTOMER_FAILED);
        }
    }


    /**
     * Updates an existing customer.
     */
    @Transactional
    @CachePut(value = "customers", key = "#id")
    @Caching(
        put = {
            @CachePut(value = "customers", key = "#id")
        },
        evict = {

            @CacheEvict(value = "customerAccounts", allEntries = true),
            @CacheEvict(value = "organizationsByCustomer", key = "#id", condition = "#result.organization != null")
        }
    )
    public CustomerDTO updateCustomer(Long id, @Valid CustomerDTO customerDTO) {
        log.info("Updating customer: {}", id);

        try {
            Customer existingCustomer = getCustomerOrThrow(id);

            // Validate update
            validator.validateCustomerUpdate(existingCustomer, customerDTO);

            // Remove existing addresses and accounts
            existingCustomer.getAddresses().forEach(address -> {
                address.setCustomer(null);
                entityManager.remove(address);
                existingCustomer.removeAddress(address);
                }
            );

            existingCustomer.getAccounts().forEach(account -> {
                account.setCustomer(null);
                entityManager.remove(account);
                existingCustomer.removeAccount(account);
                }
            );


            // Map and prepare updated entity
            Customer updatedCustomer = prepareCustomerEntity(customerDTO);
            updatedCustomer.setId(id);

            // if organization is present, set its id
            if (updatedCustomer instanceof OrganizationCustomer organizationCustomer
                && organizationCustomer.getOrganization() != null) {
                organizationCustomer.getOrganization().setId(existingCustomer.getId());
            }

            // Save updates
            updatedCustomer = customerRepository.save(updatedCustomer);

            log.info("Customer updated successfully: {}", id);
            return customerMapper.toDTOWithRelations(updatedCustomer);

        } catch (CustomerException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating customer: {}", e.getMessage(), e);
            throw new CustomerException("Failed to update customer",
                CustomerErrorCode.UPDATE_CUSTOMER_FAILED);
        }
    }


    /**
     * Deletes an existing customer.
     */
    @Transactional
    @CacheEvict(value = "customers", key = "#id")
    @Notifiable(type = NotificationType.CUSTOMER_DELETED, entityType = "CUSTOMER")
    public void deleteCustomer(Long id) {
        log.info("Deleting customer: {}", id);

        try {
            Customer customer = getCustomerOrThrow(id);

            // Validate deletion
            validator.validateCustomerDeletion(customer);

            // Delete customer
            customerRepository.delete(customer);

            log.info("Customer deleted successfully: {}", id);

        } catch (CustomerException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting customer: {}", e.getMessage(), e);
            throw new CustomerException("Failed to delete customer",
                CustomerErrorCode.DELETE_CUSTOMER_FAILED);
        }
    }


    /**
     * Gets a customer by ID.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "customers", key = "#id")
    public CustomerDTO getCustomer(Long id) {
        log.debug("Fetching customer: {}", id);
        return customerMapper.toDTOWithRelations(getCustomerOrThrow(id));
    }


    /**
     * Searches customers based on criteria.
     */
    @Transactional(readOnly = true)
    public Page<CustomerDTO> searchCustomers(@Valid CustomerSearchDTO searchDTO) {
        log.debug("Searching customers with criteria: {}", searchDTO);
        return customerRepository.search(searchDTO)
            .map(customerMapper::toDTOWithRelations);
    }


    /**
     * Gets all customers with pagination.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "customers")
    public Page<CustomerDTO> getAllCustomers(Pageable pageable) {
        log.debug("Fetching customers with pagination: {}", pageable);
        return customerRepository.findAll(pageable)
            .map(customerMapper::toDTOWithRelations);
    }


    /**
     * Gets customers by type with pagination.
     */
    @Transactional(readOnly = true)
    public Page<CustomerDTO> getCustomersByType(String type, Pageable pageable) {
        log.debug("Fetching customers by type: {} with pagination: {}", type, pageable);
        return customerRepository.findAllByType(type, pageable)
            .map(customerMapper::toDTOWithRelations);
    }

    // Helper methods

    /**
     * Gets a customer by ID or throws exception if not found.
     */
    private Customer getCustomerOrThrow(Long id) {
        validator.validateId(id);
        return customerRepository.findById(id)
            .orElseThrow(() -> new CustomerException("Customer not found",
                CustomerErrorCode.CUSTOMER_NOT_FOUND));
    }

    /**
     * Prepares a customer entity from DTO.
     */
    private Customer prepareCustomerEntity(CustomerDTO customerDTO) {
        Customer customer = customerMapper.toEntityWithRelations(customerDTO);

        // Handle organization reference if applicable
        if (customer instanceof OrganizationCustomer organizationCustomer
            && organizationCustomer.getOrganization() != null
            && organizationCustomer.getOrganization().getId() != null) {
            Organization organization = entityManager.getReference(
                Organization.class,
                organizationCustomer.getOrganization().getId()
            );
            organizationCustomer.setOrganization(organization);
        }

        // Set up bidirectional relationships
        if (customer.getAccounts() != null) {
            customer.getAccounts().forEach(account -> account.setCustomer(customer));
        }
        if (customer.getAddresses() != null) {
            customer.getAddresses().forEach(address -> address.setCustomer(customer));
        }

        return customer;
    }

}
