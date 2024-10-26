package com.digitinarytask.customer.repository;

import com.digitinarytask.customer.domain.entity.Account;
import com.digitinarytask.customer.domain.entity.Customer;
import com.digitinarytask.customer.domain.entity.IndividualCustomer;
import com.digitinarytask.customer.domain.enumeration.AccountStatus;
import com.digitinarytask.customer.domain.enumeration.AccountType;
import com.digitinarytask.customer.dto.search.AccountSearchDTO;
import com.digitinarytask.customer.dto.reposnse.CustomerAccountsSummaryDTO;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
class AccountRepositoryTest {
    @Autowired
    private EntityManager entityManager;


    @Autowired
    private AccountRepository accountRepository;

    private Account account;
    private Customer customer;

    @BeforeEach
    void setUp() {
        account = Account.builder()
            .accountNumber("123456789")
            .accountName("John Doe")
            .status(AccountStatus.ACTIVE)
            .accountType(AccountType.CURRENT)
            .balance(BigDecimal.valueOf(1000))
            .build();

        customer = IndividualCustomer.builder()
            .name("John Doe")
            .firstName("John")
            .lastName("Doe")
            .mobileNo("1234567890")
            .accounts(Collections.singleton(account))
            .nationalId("123456789")
            .build();

        account.setCustomer(customer);

        entityManager.persist(customer);
        entityManager.flush();
    }

    @Test
    void findTopAccountsByBalanceReturnsEmptyPageWhenNoAccountsExist() {
        Pageable pageable = PageRequest.of(999, 10);

        Page<Account> accounts = accountRepository.findTopAccountsByBalance(10, pageable);

        assertNotNull(accounts);
        assertTrue(accounts.isEmpty());
    }

    @Test
    void findTopAccountsByBalanceReturnsNonEmptyPageWhenAccountsExist() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<Account> accounts = accountRepository.findTopAccountsByBalance(10, pageable);

        assertNotNull(accounts);
        assertFalse(accounts.isEmpty());
    }

    @Test
    void searchReturnsEmptyPageWhenNoAccountsMatch() {
        AccountSearchDTO searchDTO = new AccountSearchDTO();
        searchDTO.setPage(0);
        searchDTO.setSize(10);
        searchDTO.setSortBy("accountNumber");
        searchDTO.setSortDirection("ASC");
        searchDTO.setAccountNumber("nonexistentAccountNumber");

        Page<Account> accounts = accountRepository.search(searchDTO);

        assertNotNull(accounts);
        assertTrue(accounts.isEmpty());
    }

    @Test
    void searchReturnsNonEmptyPageWhenAccountsMatch() {
        AccountSearchDTO searchDTO = new AccountSearchDTO();
        searchDTO.setPage(0);
        searchDTO.setSize(10);

        Page<Account> accounts = accountRepository.search(searchDTO);

        assertNotNull(accounts);
        assertFalse(accounts.isEmpty());
    }

    @Test
    void getCustomerAccountsSummaryReturnsCorrectSummary() {
        CustomerAccountsSummaryDTO summary = accountRepository.getCustomerAccountsSummary(customer.getId());

        assertNotNull(summary);
        assertEquals(customer.getAccounts().size(), summary.getTotalAccounts());
    }

    @Test
    void existsByAccountNumberReturnsTrueWhenAccountExists() {
        boolean exists = accountRepository.existsByAccountNumber("123456789");

        assertTrue(exists);
    }

    @Test
    void existsByAccountNumberReturnsFalseWhenAccountDoesNotExist() {
        boolean exists = accountRepository.existsByAccountNumber("nonexistentAccountNumber");

        assertFalse(exists);
    }
}
