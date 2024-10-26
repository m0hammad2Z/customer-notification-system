package com.digitinarytask.customer.repository.specification;

import com.digitinarytask.customer.domain.entity.Account;
import com.digitinarytask.customer.domain.entity.Address;
import com.digitinarytask.customer.domain.enumeration.AccountStatus;
import com.digitinarytask.customer.dto.search.AccountSearchDTO;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Specifications for Account entity.
 */
public class AccountSpecifications {


    public static Specification<Account> withSearchCriteria(AccountSearchDTO searchDTO) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (searchDTO.getAccountNumber() != null) {
                predicates.add(cb.like(root.get("accountNumber"),
                    "%" + searchDTO.getAccountNumber() + "%"));
            }

            if (searchDTO.getAccountName() != null) {
                predicates.add(cb.like(cb.lower(root.get("accountName")),
                    "%" + searchDTO.getAccountName().toLowerCase() + "%"));
            }

            if (searchDTO.getMinBalance() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("balance"),
                    searchDTO.getMinBalance()));
            }

            if (searchDTO.getMaxBalance() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("balance"),
                    searchDTO.getMaxBalance()));
            }

            if (searchDTO.getFromDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dateOpened"),
                    searchDTO.getFromDate()));
            }

            if (searchDTO.getToDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dateOpened"),
                    searchDTO.getToDate()));
            }

            if (searchDTO.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), searchDTO.getStatus()));
            }

            if (searchDTO.getAccountType() != null) {
                predicates.add(cb.equal(root.get("accountType"),
                    searchDTO.getAccountType()));
            }

            if (searchDTO.getCustomerId() != null) {
                predicates.add(cb.equal(root.get("customer").get("id"),
                    searchDTO.getCustomerId()));
            }

            return predicates.isEmpty() ? cb.conjunction()
                : cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Account> withStatus(AccountStatus status){
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Account> withAccountNumber(String accountNumber){
        return (root, query, cb) -> cb.equal(root.get("accountNumber"), accountNumber);
    }

    public static Specification<Account> withCustomerId(Long customerId){
        return (root, query, cb) -> cb.equal(root.get("customer").get("id"), customerId);
    }

    public static Specification<Account> withBalanceGt(BigDecimal balance) {
        return (root, query, cb) -> cb.greaterThan(root.get("balance"), balance);
    }
}
