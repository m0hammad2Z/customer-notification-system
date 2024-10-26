package com.digitinarytask.customer.service;

import com.digitinarytask.shared.annotation.Notifiable;
import com.digitinarytask.customer.domain.entity.Account;
import com.digitinarytask.customer.domain.entity.Customer;
import com.digitinarytask.customer.domain.enumeration.error.AccountErrorCode;
import com.digitinarytask.customer.dto.domain.AccountDTO;
import com.digitinarytask.customer.dto.mapper.AccountMapper;
import com.digitinarytask.customer.dto.search.AccountSearchDTO;
import com.digitinarytask.customer.dto.reposnse.CustomerAccountsSummaryDTO;
import com.digitinarytask.customer.exception.AccountException;
import com.digitinarytask.customer.repository.AccountRepository;
import com.digitinarytask.customer.repository.CustomerRepository;
import com.digitinarytask.customer.service.validation.AccountValidationService;
import com.digitinarytask.shared.enumeration.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Validated
@Slf4j
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final AccountMapper accountMapper;
    private final Lock balanceUpdateLock = new ReentrantLock();
    private final AccountValidationService accountValidationService;


    /**
     * Creates a new account for a customer.
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "topAccounts", allEntries = true),
        @CacheEvict(value = "customers", key = "#customerId")
    })
    @CachePut(value = "accounts", key = "#result.id")
    @Notifiable(type = NotificationType.ACCOUNT_CREATED, entityType = "ACCOUNT")
    public AccountDTO createAccount(Long customerId, AccountDTO accountDTO) {
        log.info("Creating new account for customer: {}", customerId);

        accountValidationService.validateId(customerId);

        try {
            // Get customer or throw
            Customer customer = getCustomerOrThrow(customerId);

            // Validate business rules
            accountValidationService.validateNewAccount(accountDTO);

            // Check for existing account number
            checkDuplicateAccountNumber(accountDTO.getAccountNumber());

            // Create and save account
            Account account = accountMapper.toEntity(accountDTO);
            account.setCustomer(customer);
            account = accountRepository.save(account);

            log.info("Account created successfully with ID: {}", account.getId());
            return accountMapper.toDTO(account);

        } catch (AccountException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error creating account: {}", e.getMessage(), e);
            throw new AccountException("Failed to create account",
                AccountErrorCode.ACCOUNT_CREATION_ERROR);
        }
    }

    /**
     * Updates an existing account.
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "topAccounts", allEntries = true),
        @CacheEvict(value = "accounts", key = "#id"),
        @CacheEvict(value = "customers", key = "#result.customerId") // Ensure #result.customerId is valid
    })
    @Notifiable(type = NotificationType.ACCOUNT_UPDATED, entityType = "ACCOUNT")
    public AccountDTO updateAccount(Long id, AccountDTO accountDTO) {
        log.info("Updating account: {}", id);

        accountValidationService.validateId(id);

        try {
            // Get existing account
            Account existingAccount = getAccountOrThrow(id);

            // Validate update
            accountValidationService.validateAccountUpdate(existingAccount, accountDTO);

            // Update account
            Account updatedAccount = accountMapper.toEntity(accountDTO);
            updatedAccount.setId(id);
            updatedAccount.setCustomer(existingAccount.getCustomer());
            updatedAccount = accountRepository.save(updatedAccount);

            log.info("Account updated successfully: {}", id);
            return accountMapper.toDTO(updatedAccount);

        } catch (AccountException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error updating account: {}", e.getMessage(), e);
            throw new AccountException("Failed to update account",
                AccountErrorCode.ACCOUNT_UPDATE_ERROR);
        }
    }


    /**
     * Updates the balance of an account.
     */
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "customerAccounts", key = "#account.customer.id"), // Ensure correct customer ID is used
        @CacheEvict(value = "topAccounts", allEntries = true),
        @CacheEvict(value = "accounts", key = "#accountId")
    })
    @Notifiable(type = NotificationType.ACCOUNT_BALANCE_UPDATED, entityType = "ACCOUNT")
    public void updateBalance(Long accountId, BigDecimal amount) {
        balanceUpdateLock.lock();
        try {
            log.info("Updating balance for account: {}", accountId);

            accountValidationService.validateId(accountId);

            Account account = getAccountOrThrow(accountId);

            // Validate balance update
            accountValidationService.validateBalanceUpdate(account.getBalance(), amount);

            // Update balance
            account.setBalance(account.getBalance().add(amount));
            accountRepository.save(account);

        } finally {
            balanceUpdateLock.unlock();
        }
    }


    /**
     * Deletes an account.
     */
    @Transactional
    @CacheEvict(value = "accounts", key = "#id")
    @Notifiable(type = NotificationType.ACCOUNT_DELETED, entityType = "ACCOUNT")
    public void deleteAccount(Long id) {
        log.info("Deleting account: {}", id);

        try {
            accountValidationService.validateId(id);

            Account account = getAccountOrThrow(id);

            // Validate deletion
            accountValidationService.validateAccountDeletion(account);

            // Delete account
            accountRepository.deleteById(id);


        } catch (AccountException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting account: {}", e.getMessage(), e);
            throw new AccountException("Failed to delete account",
                AccountErrorCode.ACCOUNT_DELETION_ERROR);
        }
    }


    /**
     * Fetches an account by ID.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "accounts", key = "#id")
    public AccountDTO getAccount(Long id) {
        log.debug("Fetching account: {}", id);

        accountValidationService.validateId(id);
        return accountMapper.toDTO(getAccountOrThrow(id));
    }


    /**
     * Fetches all accounts for a customer.
     */
    @Transactional(readOnly = true)
    public Page<AccountDTO> getCustomerAccounts(Long customerId, Pageable pageable) {
        log.debug("Fetching accounts for customer: {}", customerId);

        accountValidationService.validateId(customerId);

        getCustomerOrThrow(customerId);
        return accountRepository.findByCustomerId(customerId, pageable)
            .map(accountMapper::toDTO);
    }


    /**
     * Fetches the account summary for a customer.
     */
    @Transactional(readOnly = true)
    public CustomerAccountsSummaryDTO getCustomerAccountsSummary(Long customerId) {
        log.debug("Fetching account summary for customer: {}", customerId);

        accountValidationService.validateId(customerId);
        getCustomerOrThrow(customerId);
        return accountRepository.getCustomerAccountsSummary(customerId);
    }


    /**
     * Fetches the top accounts by balance.
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "topAccounts", key = "#limit")
    public Page<AccountDTO> findTopAccountsByBalance(int limit, Pageable pageable) {
        log.debug("Fetching top {} accounts by balance", limit);
        Page<Account> page = accountRepository.findTopAccountsByBalance(limit, pageable);
        return page.map(accountMapper::toDTO);
    }


    /**
     * Searches accounts based on criteria.
     */
    @Transactional(readOnly = true)
    public Page<AccountDTO> search(AccountSearchDTO searchDTO) {
        log.debug("Searching accounts with criteria: {}", searchDTO);
        Page<Account> page = accountRepository.search(searchDTO);
        return page.map(accountMapper::toDTO);
    }


    // Helper methods

    /**
     * Fetches a customer by ID or throws an exception if not found.
     */
    private Customer getCustomerOrThrow(Long customerId) {
        return customerRepository.findById(customerId)
            .orElseThrow(() -> new AccountException("Customer not found",
                AccountErrorCode.CUSTOMER_NOT_FOUND));
    }

    /**
     * Fetches an account by ID or throws an exception if not found.
     */
    private Account getAccountOrThrow(Long accountId) {
        return accountRepository.findById(accountId)
            .orElseThrow(() -> new AccountException("Account not found",
                AccountErrorCode.ACCOUNT_NOT_FOUND));
    }

    /**
     * Checks if an account number already exists.
     */
    private void checkDuplicateAccountNumber(String accountNumber) {
        if (accountRepository.existsByAccountNumber(accountNumber)) {
            throw new AccountException("Account number already exists",
                AccountErrorCode.DUPLICATE_ACCOUNT_NUMBER);
        }
    }
}
