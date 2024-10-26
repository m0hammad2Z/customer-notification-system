package com.digitinarytask.customer.repository.impl;

import com.digitinarytask.customer.domain.entity.Customer;
import com.digitinarytask.customer.domain.entity.IndividualCustomer;
import com.digitinarytask.customer.domain.entity.OrganizationCustomer;
import com.digitinarytask.customer.dto.search.CustomerSearchDTO;
import com.digitinarytask.customer.repository.AbstractSpecificationRepository;
import com.digitinarytask.customer.repository.custom.CustomerRepositoryCustom;
import com.digitinarytask.customer.repository.specification.CustomerSpecifications;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.*;

import java.util.List;


/**
 * Custom repository implementation for Customer entity.
 */
public class CustomerRepositoryImpl extends AbstractSpecificationRepository<Customer>
    implements CustomerRepositoryCustom {


    public CustomerRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }

    /**
     * Searches for customers based on search criteria.
     */
    @Override
    public Page<Customer> search(CustomerSearchDTO searchDTO) {
        Sort sort = searchDTO.getSortBy() != null
            ? Sort.by(Sort.Direction.fromString(searchDTO.getSortDirection()), searchDTO.getSortBy())
            : Sort.unsorted();

        PageRequest pageRequest = PageRequest.of(
            searchDTO.getPage(),
            searchDTO.getSize(),
            sort
        );

        return findBySpecification(CustomerSpecifications.withSearchCriteria(searchDTO), pageRequest, Customer.class);
    }

    /**
     * Finds customers by type.
     */
    @Override
    public Page<Customer> findAllByType(String type, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Customer> query = cb.createQuery(Customer.class);
        Root<Customer> root = query.from(Customer.class);

        if ("INDIVIDUAL".equals(type)) {
            query.select(root).where(cb.equal(root.type(), IndividualCustomer.class));
        } else if ("ORGANIZATION".equals(type)) {
            query.select(root).where(cb.equal(root.type(), OrganizationCustomer.class));
        } else {
            return new PageImpl<>(List.of(), pageable, 0);
        }

        List<Customer> result = entityManager.createQuery(query)
            .setFirstResult((int) pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();

        return new PageImpl<>(result, pageable, result.size());
    }

    /**
     * Checks if a customer with the given national ID exists.
     */
    @Override
    public boolean existsByNationalId(String nationalId) {
        return existsBySpecification(
            CustomerSpecifications.withNationalId(nationalId),
            Customer.class
        );
    }

}

