package com.digitinarytask.customer.repository.custom;

import com.digitinarytask.customer.domain.entity.Address;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Custom repository interface for Address entity.
 */
public interface AddressRepositoryCustom {

    /**
     * Finds addresses by customer ID.
     */
    Page<Address> findByCustomerId(Long customerId, Pageable pageable);
}
