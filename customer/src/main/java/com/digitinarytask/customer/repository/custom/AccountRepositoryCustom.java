package com.digitinarytask.customer.repository.custom;

import com.digitinarytask.customer.domain.entity.Account;
import com.digitinarytask.customer.dto.search.AccountSearchDTO;
import com.digitinarytask.customer.dto.reposnse.CustomerAccountsSummaryDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Custom repository interface for Account entity.
 */
public interface AccountRepositoryCustom {

    /**
     * Finds the top accounts by balance.
     */
    Page<Account> findTopAccountsByBalance(int limit, Pageable pageable);

    /**
     * Searches for accounts based on search criteria.
     */
    Page<Account> search(AccountSearchDTO searchDTO);

    /**
     * Finds accounts by customer ID.
     */
    Page<Account> findByCustomerId(Long customerId, Pageable pageable);

    /**
     * Retrieves a summary of accounts for a customer.
     */
    CustomerAccountsSummaryDTO getCustomerAccountsSummary(Long customerId);

    /**
     * Checks if an account number already exists.
     */
    boolean existsByAccountNumber(String accountNumber);
}
