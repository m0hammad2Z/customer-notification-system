package com.digitinarytask.customer.repository.impl;

import com.digitinarytask.customer.domain.entity.Account;
import com.digitinarytask.customer.domain.enumeration.AccountStatus;
import com.digitinarytask.customer.dto.search.AccountSearchDTO;
import com.digitinarytask.customer.dto.reposnse.CustomerAccountsSummaryDTO;
import com.digitinarytask.customer.repository.AbstractSpecificationRepository;
import com.digitinarytask.customer.repository.custom.AccountRepositoryCustom;
import com.digitinarytask.customer.repository.specification.AccountSpecifications;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Custom repository implementation for Account entity.
 */
public class AccountRepositoryImpl extends AbstractSpecificationRepository<Account> implements AccountRepositoryCustom {

    public AccountRepositoryImpl(EntityManager entityManager) {
        super(entityManager);
    }


    /**
     * Retrieves a summary of accounts for a customer.
     */
    @Override
    public CustomerAccountsSummaryDTO getCustomerAccountsSummary(Long customerId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CustomerAccountsSummaryDTO> query = cb.createQuery(CustomerAccountsSummaryDTO.class);
        Root<Account> root = query.from(Account.class);

        // Predicates
        Predicate customerPredicate = cb.equal(root.get("customer").get("id"), customerId);

        // Subqueries for status counts
        Subquery<Long> activeCountSubquery = createStatusCountSubquery(cb, customerId, AccountStatus.ACTIVE);
        Subquery<Long> inactiveCountSubquery = createStatusCountSubquery(cb, customerId, AccountStatus.INACTIVE);

        query.multiselect(
            cb.count(root).as(Long.class),
            cb.sum(root.get("balance")).as(BigDecimal.class),
            cb.avg(root.get("balance")).as(BigDecimal.class),
            activeCountSubquery.as(Long.class),
            inactiveCountSubquery.as(Long.class)
        ).where(customerPredicate);

        List<CustomerAccountsSummaryDTO> results = entityManager.createQuery(query).getResultList();

        return results.isEmpty() ? null : results.get(0);
    }

    /**
     * Creates a subquery to count accounts with a specific status.
     */
    private Subquery<Long> createStatusCountSubquery(CriteriaBuilder cb, Long customerId, AccountStatus status) {
        Subquery<Long> subquery = cb.createQuery().subquery(Long.class);
        Root<Account> subRoot = subquery.from(Account.class);
        subquery.select(cb.count(subRoot))
            .where(cb.and(
                cb.equal(subRoot.get("customer").get("id"), customerId),
                cb.equal(subRoot.get("status"), status)
            ));
        return subquery;
    }


    /**
     * Finds the top accounts by balance.
     */
    @Override
    public Page<Account> findTopAccountsByBalance(int limit, Pageable pageable) {
        return findBySpecification(
            AccountSpecifications.withStatus(AccountStatus.ACTIVE).and(AccountSpecifications.withBalanceGt(BigDecimal.valueOf(limit))),
            PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()),
            Account.class
        );
    }

    /**
     * Searches for accounts based on search criteria.
     */
    @Override
    public Page<Account> search(AccountSearchDTO searchDTO) {
        Sort sort = searchDTO.getSortBy() != null
            ? Sort.by(Sort.Direction.fromString(searchDTO.getSortDirection()), searchDTO.getSortBy())
            : Sort.unsorted();

        PageRequest pageRequest = PageRequest.of(
            searchDTO.getPage(),
            searchDTO.getSize(),
            sort
        );

        return findBySpecification(
            AccountSpecifications.withSearchCriteria(searchDTO),
            pageRequest,
            Account.class
        );
    }

    /**
     * Checks if an account number already exists.
     */
    @Override
    public boolean existsByAccountNumber(String accountNumber) {
       return existsBySpecification(AccountSpecifications.withAccountNumber(accountNumber), Account.class);
    }

    /**
     * Finds accounts by customer ID.
     */
    @Override
    public Page<Account> findByCustomerId(Long customerId, Pageable pageable) {
        return findBySpecification(
            AccountSpecifications.withCustomerId(customerId),
            PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()),
            Account.class
        );
    }
}
