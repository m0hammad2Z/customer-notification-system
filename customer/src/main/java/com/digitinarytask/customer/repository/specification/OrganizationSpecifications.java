package com.digitinarytask.customer.repository.specification;

import com.digitinarytask.customer.domain.entity.Organization;
import com.digitinarytask.customer.dto.search.OrganizationSearchDTO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * Specifications for Organization entity.
 */
public class OrganizationSpecifications {
    public static Specification<Organization> withSearchCriteria(OrganizationSearchDTO searchDTO) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchDTO.getRegistrationNumber() != null) {
                predicates.add(criteriaBuilder.equal(root.get("registrationNumber"), searchDTO.getRegistrationNumber()));
            }

            if (searchDTO.getLegalName() != null) {
                predicates.add(criteriaBuilder.like(root.get("legalName"), "%" + searchDTO.getLegalName() + "%"));
            }

            if (searchDTO.getTrademarkName() != null) {
                predicates.add(criteriaBuilder.like(root.get("trademarkName"), "%" + searchDTO.getTrademarkName() + "%"));
            }

            return predicates.isEmpty() ? null : criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Organization> withId(Long id) {
        return (root, query, builder) -> builder.equal(root.get("id"), id);
    }

    public static Specification<Organization> withRegistrationNumber(String registrationNumber) {
        return (root, query, builder) -> builder.equal(root.get("registrationNumber"), registrationNumber);
    }

    public static Specification<Organization> withCustomerId(Long id) {
        return (root, query, builder) -> builder.equal(root.get("organizationCustomer").get("id"), id);
    }
}
