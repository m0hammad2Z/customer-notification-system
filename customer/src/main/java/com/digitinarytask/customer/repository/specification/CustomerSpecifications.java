package com.digitinarytask.customer.repository.specification;

import com.digitinarytask.customer.domain.entity.Customer;
import com.digitinarytask.customer.dto.search.CustomerSearchDTO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Specifications for Customer entity.
 */
public class CustomerSpecifications {

    public static Specification<Customer> withSearchCriteria(CustomerSearchDTO searchDTO) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(searchDTO.getName())) {
                predicates.add(cb.like(cb.lower(root.get("name")),
                    "%" + searchDTO.getName().toLowerCase() + "%"));
            }

            if (StringUtils.hasText(searchDTO.getEmail())) {
                predicates.add(cb.equal(cb.lower(root.get("email")),
                    searchDTO.getEmail().toLowerCase()));
            }

            if (StringUtils.hasText(searchDTO.getMobile())) {
                predicates.add(cb.equal(root.get("mobileNo"), searchDTO.getMobile()));
            }

            if (StringUtils.hasText(searchDTO.getCity())) {
                predicates.add(cb.equal(cb.lower(root.get("address").get("city")),
                    searchDTO.getCity().toLowerCase()));
            }

            if (StringUtils.hasText(searchDTO.getCountry())) {
                predicates.add(cb.equal(cb.lower(root.get("address").get("country")),
                    searchDTO.getCountry().toLowerCase()));
            }

            return predicates.isEmpty() ? null : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Customer> withName(String name) {
        return (root, query, cb) ->
            cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Customer> withEmail(String email) {
        return (root, query, cb) ->
            cb.equal(cb.lower(root.get("email")), email.toLowerCase());
    }

    public static Specification<Customer> withMobile(String mobile) {
        return (root, query, cb) ->
            cb.equal(root.get("mobileNo"), mobile);
    }

    public static Specification<Customer> withNationalId(String nationalId) {
        return (root, query, cb) ->
            cb.equal(root.get("nationalId"), nationalId);
    }

    public static Specification<Customer> withCity(String city) {
        return (root, query, cb) ->
            cb.equal(cb.lower(root.get("address").get("city")), city.toLowerCase());
    }

    public static Specification<Customer> withCountry(String country) {
        return (root, query, cb) ->
            cb.equal(cb.lower(root.get("address").get("country")), country.toLowerCase());
    }

    public static Specification<Customer> withType(String country) {
        return (root, query, cb) ->
            cb.equal(cb.lower(root.get("type")), country.toLowerCase());
    }

}
