package com.digitinarytask.customer.repository.custom;

import com.digitinarytask.customer.domain.entity.Customer;
import com.digitinarytask.customer.dto.search.CustomerSearchDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Custom repository interface for Customer entity.
 */
public interface CustomerRepositoryCustom{
    /**
     * Searches for customers based on search criteria.
     */
    Page<Customer> search(CustomerSearchDTO searchDTO);
    /**
     * Finds customers by type.
     */
    Page<Customer> findAllByType(String type, Pageable pageable);
    /**
     * Finds customers by type.
     */
    boolean existsByNationalId(String nationalId);
}
