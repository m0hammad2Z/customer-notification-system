package com.digitinarytask.customer.service;

import com.digitinarytask.customer.domain.entity.Account;
import com.digitinarytask.customer.domain.entity.Customer;
import com.digitinarytask.customer.domain.enumeration.AccountStatus;
import com.digitinarytask.customer.domain.enumeration.AccountType;
import com.digitinarytask.customer.domain.enumeration.error.AccountErrorCode;
import com.digitinarytask.customer.dto.domain.AccountDTO;
import com.digitinarytask.customer.dto.search.AccountSearchDTO;
import com.digitinarytask.customer.exception.AccountException;
import com.digitinarytask.customer.repository.AccountRepository;
import com.digitinarytask.customer.repository.CustomerRepository;
import com.digitinarytask.customer.dto.mapper.AccountMapper;
import com.digitinarytask.customer.service.validation.AccountValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private AccountMapper accountMapper;
    @Mock
    private AccountValidationService validator;


    @InjectMocks
    private AccountService accountService;

    private Customer testCustomer;
    private Account testAccount;
    private AccountDTO testAccountDTO;

    @BeforeEach
    void setUp() {
        testCustomer = new Customer();
        testCustomer.setId(1L);

        testAccount = Account.builder()
            .id(1L)
            .accountNumber("AC12345678")
            .balance(BigDecimal.valueOf(1000))
            .accountType(AccountType.CURRENT)
            .status(AccountStatus.ACTIVE)
            .customer(testCustomer).build();

        testAccountDTO = AccountDTO.builder()
            .id(1L)
            .accountNumber("AC12345678")
            .balance(BigDecimal.valueOf(1000))
            .accountType(AccountType.CURRENT)
            .status(AccountStatus.ACTIVE)
            .build();
    }

    @Test
    void createAccount_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.existsByAccountNumber(any())).thenReturn(false);
        when(accountMapper.toEntity(any(AccountDTO.class))).thenReturn(testAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(accountMapper.toDTO(any(Account.class))).thenReturn(testAccountDTO);

        AccountDTO result = accountService.createAccount(1L, testAccountDTO);

        assertNotNull(result);
        assertEquals(testAccountDTO.getAccountNumber(), result.getAccountNumber());
        verify(validator).validateNewAccount(any(AccountDTO.class));
    }

    @Test
    void createAccount_ThrowsExceptionOnDuplicateAccountNumber() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.existsByAccountNumber(any())).thenReturn(true);

        assertThrows(AccountException.class, () -> accountService.createAccount(1L, testAccountDTO));
        verify(validator).validateNewAccount(any(AccountDTO.class));
    }

    @Test
    void updateAccount_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountMapper.toEntity(any(AccountDTO.class))).thenReturn(testAccount);
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);
        when(accountMapper.toDTO(any(Account.class))).thenReturn(testAccountDTO);

        AccountDTO result = accountService.updateAccount(1L, testAccountDTO);

        assertNotNull(result);
        assertEquals(testAccountDTO.getAccountNumber(), result.getAccountNumber());
        verify(validator).validateAccountUpdate(any(Account.class), any(AccountDTO.class));
    }

    @Test
    void updateBalance_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        doNothing().when(validator).validateBalanceUpdate(any(), any());

        accountService.updateBalance(1L, BigDecimal.valueOf(500));

        assertEquals(BigDecimal.valueOf(1500), testAccount.getBalance());
    }

    @Test
    void updateBalance_ThrowsExceptionOnInvalidBalance() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        doThrow(new AccountException("Invalid balance update", AccountErrorCode.INSUFFICIENT_BALANCE))
            .when(validator).validateBalanceUpdate(any(), any());

        assertThrows(AccountException.class, () -> accountService.updateBalance(1L, BigDecimal.valueOf(-2000)));
    }

    @Test
    void deleteAccount_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        accountService.deleteAccount(1L);

        verify(accountRepository).deleteById(1L);
    }

    @Test
    void deleteAccount_ThrowsExceptionIfAccountNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AccountException.class, () -> accountService.deleteAccount(1L));
    }

    @Test
    void getCustomerAccounts_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Account> page = new PageImpl<>(Collections.singletonList(testAccount), pageable, 1);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(testCustomer));
        when(accountRepository.findByCustomerId(1L, pageable)).thenReturn(page);
        when(accountMapper.toDTO(any(Account.class))).thenReturn(testAccountDTO);

        Page<AccountDTO> result = accountService.getCustomerAccounts(1L, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(testAccountDTO.getAccountNumber(), result.getContent().get(0).getAccountNumber());
    }

    @Test
    void findTopAccountsByBalance_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Account> page = new PageImpl<>(Collections.singletonList(testAccount), pageable, 1);
        when(accountRepository.findTopAccountsByBalance(1, pageable)).thenReturn(page);
        when(accountMapper.toDTO(any(Account.class))).thenReturn(testAccountDTO);

        Page<AccountDTO> result = accountService.findTopAccountsByBalance(1, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(testAccountDTO.getAccountNumber(), result.getContent().get(0).getAccountNumber());
    }

    @Test
    void getAccount_Success() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));
        when(accountMapper.toDTO(any(Account.class))).thenReturn(testAccountDTO);

        AccountDTO result = accountService.getAccount(1L);

        assertNotNull(result);
        assertEquals(testAccountDTO.getAccountNumber(), result.getAccountNumber());
    }

    @Test
    void getAccount_ThrowsExceptionIfNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(AccountException.class, () -> accountService.getAccount(1L));
    }

    @Test
    void searchAccount_ReturnsMatchingAccounts() {
        AccountSearchDTO searchDTO = new AccountSearchDTO();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Account> page = new PageImpl<>(Collections.singletonList(testAccount), pageable, 1);
        when(accountRepository.search(searchDTO)).thenReturn(page);
        when(accountMapper.toDTO(any(Account.class))).thenReturn(testAccountDTO);

        Page<AccountDTO> result = accountService.search(searchDTO);

        assertEquals(1, result.getTotalElements());
        assertEquals(testAccountDTO.getAccountNumber(), result.getContent().get(0).getAccountNumber());
    }
}
