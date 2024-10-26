package com.digitinarytask.customer.repository.specification;

import com.digitinarytask.customer.domain.entity.Address;
import com.digitinarytask.customer.domain.enumeration.AddressType;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications for Address entity.
 */
public class AddressSpecifications {
    public static Specification<Address> withId(Long id) {
        return (root, query, builder) -> builder.equal(root.get("id"), id);
    }

    public static Specification<Address> withPostalCode(String postalCode) {
        return (root, query, builder) -> builder.equal(root.get("postalCode"), postalCode);
    }

    public static Specification<Address> withCity(String city) {
        return (root, query, builder) -> builder.equal(root.get("city"), city);
    }

    public static Specification<Address> withCountry(String country) {
        return (root, query, builder) -> builder.equal(root.get("country"), country);
    }

    public static Specification<Address> withStreet(String street) {
        return (root, query, builder) -> builder.equal(root.get("street"), street);
    }

    public static Specification<Address> withAddressType(AddressType addressType) {
        return (root, query, builder) -> builder.equal(root.get("addressType"), addressType);
    }

    public static Specification<Address> withCustomerId(Long customerId){
        return (root, query, builder) -> builder.equal(root.get("customer").get("id"), customerId);
    }

}
