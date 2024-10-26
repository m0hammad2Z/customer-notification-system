package com.digitinarytask.customer.repository.impl;

import com.digitinarytask.customer.domain.entity.Address;
import com.digitinarytask.customer.repository.AbstractSpecificationRepository;
import com.digitinarytask.customer.repository.custom.AddressRepositoryCustom;
import com.digitinarytask.customer.repository.specification.AddressSpecifications;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Custom repository implementation for Address entity.
 */

public class AddressRepositoryImpl extends AbstractSpecificationRepository<Address> implements AddressRepositoryCustom {
    public AddressRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }


    /**
     * Finds addresses by customer ID.
     */
    @Override
    public Page<Address> findByCustomerId(Long customerId, Pageable pageable) {
        return findBySpecification(
            AddressSpecifications.withCustomerId(customerId),
            PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()),
            Address.class);
    }
}
