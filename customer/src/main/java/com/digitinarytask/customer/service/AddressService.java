package com.digitinarytask.customer.service;

import com.digitinarytask.customer.domain.entity.Address;
import com.digitinarytask.customer.domain.entity.Customer;
import com.digitinarytask.customer.domain.enumeration.error.AddressErrorCode;
import com.digitinarytask.customer.domain.enumeration.error.CustomerErrorCode;
import com.digitinarytask.customer.dto.domain.AddressDTO;
import com.digitinarytask.customer.dto.mapper.AddressMapper;
import com.digitinarytask.customer.exception.AddressException;
import com.digitinarytask.customer.exception.CustomerException;
import com.digitinarytask.customer.repository.AddressRepository;
import com.digitinarytask.customer.repository.CustomerRepository;
import com.digitinarytask.customer.service.validation.AddressValidationService;
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
import jakarta.validation.constraints.NotNull;

/**
 * Service for managing customer addresses.
 */
@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class AddressService {
    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;
    private final AddressMapper addressMapper;
    private final AddressValidationService addressValidationService;

    /**
     * Retrieves all addresses for a given customer.
     */
    @Transactional(readOnly = true)
    public Page<AddressDTO> findCustomerAddresses(
        @NotNull Long customerId,
        @NotNull Pageable pageable) {
        log.debug("Finding addresses for customer ID: {}", customerId);

        addressValidationService.validateId(customerId);

        // Check customer existence
        getCustomerOrThrow(customerId);

        try {
            return addressRepository.findByCustomerId(customerId, pageable)
                .map(addressMapper::toDTO);
        } catch (Exception e) {
            log.error("Error retrieving addresses for customer {}: {}", customerId, e.getMessage());
            throw new AddressException(
                "Failed to retrieve customer addresses",
                AddressErrorCode.ADDRESS_RETRIEVAL_ERROR
            );
        }
    }

    /**
     * Adds a new address for a customer.
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "customers", key = "#customerId"),
    })
    @CachePut(value = "addresses", key = "#customerId")
    public AddressDTO addCustomerAddress(
        @NotNull Long customerId,
        @NotNull @Valid AddressDTO addressDTO) {
        log.debug("Adding address for customer ID: {}", customerId);

        addressValidationService.validateId(customerId);

        // Check customer existence
        Customer customer = getCustomerOrThrow(customerId);

        // Validate business rules
        addressValidationService.validateAddressDTO(addressDTO);

        try {
            Address address = addressMapper.toEntity(addressDTO);
            address.setCustomer(customer);

            Address savedAddress = addressRepository.save(address);
            log.info("Successfully added address ID: {} for customer ID: {}",
                savedAddress.getId(), customerId);

            return addressMapper.toDTO(savedAddress);
        } catch (Exception e) {
            log.error("Error adding address for customer {}: {}", customerId, e.getMessage());
            throw new AddressException(
                "Failed to add customer address",
                AddressErrorCode.ADDRESS_CREATION_ERROR
            );
        }
    }

    /**
     * Updates an existing customer address.
     */
    @Transactional
    @CachePut(value = "addresses", key = "#customerId")
    @Caching(evict = {
        @CacheEvict(value = "customers", key = "#customerId")
    })
    public AddressDTO updateCustomerAddress(
        @NotNull Long customerId,
        @NotNull Long addressId,
        @NotNull @Valid AddressDTO addressDTO) {
        log.debug("Updating address ID: {} for customer ID: {}", addressId, customerId);

        addressValidationService.validateId(customerId);
        addressValidationService.validateId(customerId);

        // Check both customer and address existence
        Customer customer = getCustomerOrThrow(customerId);
        Address existingAddress = getAddressOrThrow(addressId);

        // Verify address belongs to customer
        validateAddressOwnership(existingAddress, customerId);

        // Validate business rules
        addressValidationService.validateAddressDTO(addressDTO);

        try {
            Address updatedAddress = addressMapper.toEntity(addressDTO);
            updatedAddress.setId(addressId);
            updatedAddress.setCustomer(customer);

            Address savedAddress = addressRepository.save(updatedAddress);
            log.info("Successfully updated address ID: {} for customer ID: {}",
                addressId, customerId);

            return addressMapper.toDTO(savedAddress);
        } catch (Exception e) {
            log.error("Error updating address {} for customer {}: {}",
                addressId, customerId, e.getMessage());
            throw new AddressException(
                "Failed to update customer address",
                AddressErrorCode.ADDRESS_UPDATE_ERROR
            );
        }
    }

    /**
     * Deletes a customer address.
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "addresses", allEntries = true),
        @CacheEvict(value = "customers", allEntries = true)
    })
    public void deleteCustomerAddress(@NotNull Long addressId) {
        log.debug("Deleting address ID: {}", addressId);

        addressValidationService.validateId(addressId);

         addressRepository.findById(addressId)
             .orElseThrow(() -> new AddressException(
                 "Address not found",
                 AddressErrorCode.ADDRESS_NOT_FOUND
             ));

        try {
            addressRepository.deleteById(addressId);
            log.info("Successfully deleted address ID: {}", addressId);
        } catch (Exception e) {
            log.error("Error deleting address {}: {}", addressId, e.getMessage());
            throw new AddressException(
                "Failed to delete customer address",
                AddressErrorCode.ADDRESS_DELETION_ERROR
            );
        }
    }

    /**
     * Gets customer by ID or throws exception if not found.
     */
    private Customer getCustomerOrThrow(Long customerId) {
        return customerRepository.findById(customerId)
            .orElseThrow(() -> new CustomerException(
                String.format("Customer with ID %d not found", customerId),
                CustomerErrorCode.CUSTOMER_NOT_FOUND
            ));
    }

    /**
     * Gets address by ID or throws exception if not found.
     */
    private Address getAddressOrThrow(Long addressId) {
        return addressRepository.findById(addressId)
            .orElseThrow(() -> new AddressException(
                String.format("Address with ID %d not found", addressId),
                AddressErrorCode.ADDRESS_NOT_FOUND
            ));
    }

    /**
     * Validates that the address belongs to the specified customer.
     */
    private void validateAddressOwnership(Address address, Long customerId) {
        if (!address.getCustomer().getId().equals(customerId)) {
            throw new AddressException(
                String.format("Address %d does not belong to customer %d",
                    address.getId(), customerId),
                AddressErrorCode.ADDRESS_NOT_FOUND
            );
        }
    }
}
