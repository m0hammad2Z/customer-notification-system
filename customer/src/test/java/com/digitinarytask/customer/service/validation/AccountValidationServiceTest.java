package com.digitinarytask.customer.service.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.digitinarytask.customer.domain.entity.Account;
import com.digitinarytask.customer.domain.enumeration.AccountStatus;
import com.digitinarytask.customer.domain.enumeration.AccountType;
import com.digitinarytask.customer.domain.enumeration.error.AccountErrorCode;
import com.digitinarytask.customer.dto.domain.AccountDTO;
import com.digitinarytask.customer.exception.AccountException;
import com.digitinarytask.customer.service.validation.AccountValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class AccountValidationServiceTest {

    @InjectMocks
    private AccountValidationService accountValidationService;

    @Mock
    private Account existingAccount;

    @Mock
    private AccountDTO accountDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void validateId_validId_doesNotThrowException() {
        assertDoesNotThrow(() -> accountValidationService.validateId(1L));
    }
    @Test
    void validateId_nullId_throwsAccountException() {
        AccountException exception = assertThrows(AccountException.class, () -> accountValidationService.validateId(null));
        assertEquals(AccountErrorCode.INVALID_ACCOUNT_ID, exception.getErrorCode());
    }
    @Test
    void validateNewAccount_validAccount_doesNotThrowException() {
        when(accountDTO.getAccountNumber()).thenReturn("AB12345678");
        when(accountDTO.getAccountName()).thenReturn("John Doe");
        when(accountDTO.getBalance()).thenReturn(BigDecimal.valueOf(1000));
        when(accountDTO.getAccountType()).thenReturn(AccountType.CURRENT);
        when(accountDTO.getDateOpened()).thenReturn(LocalDate.now());
        when(accountDTO.getStatus()).thenReturn(AccountStatus.ACTIVE);

        assertDoesNotThrow(() -> accountValidationService.validateNewAccount(accountDTO));
    }
    @Test
    void validateNewAccount_nullAccount_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> accountValidationService.validateNewAccount(null));
        assertEquals("Account data cannot be null", exception.getMessage());
    }
    @Test
    void validateAccountUpdate_validUpdate_doesNotThrowException() {
        when(existingAccount.getId()).thenReturn(1L);
        when(existingAccount.getAccountNumber()).thenReturn("AB12345678");
        when(existingAccount.getStatus()).thenReturn(AccountStatus.ACTIVE);
        when(accountDTO.getAccountNumber()).thenReturn("AB12345678");
        when(accountDTO.getAccountName()).thenReturn("John Doe");
        when(accountDTO.getStatus()).thenReturn(AccountStatus.ACTIVE);
        when(accountDTO.getAccountType()).thenReturn(AccountType.CURRENT);

        assertDoesNotThrow(() -> accountValidationService.validateAccountUpdate(existingAccount, accountDTO));
    }
    @Test
    void validateAccountUpdate_nullUpdateDTO_throwsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> accountValidationService.validateAccountUpdate(existingAccount, null));
        assertEquals("Update data cannot be null", exception.getMessage());
    }
    @Test
    void validateBalanceUpdate_validBalanceUpdate_doesNotThrowException() {
        assertDoesNotThrow(() -> accountValidationService.validateBalanceUpdate(BigDecimal.valueOf(1000), BigDecimal.valueOf(500)));
    }
    @Test
    void validateBalanceUpdate_negativeBalance_throwsAccountException() {
        AccountException exception = assertThrows(AccountException.class, () -> accountValidationService.validateBalanceUpdate(BigDecimal.valueOf(1000), BigDecimal.valueOf(-1500)));
        assertEquals(AccountErrorCode.INSUFFICIENT_BALANCE, exception.getErrorCode());
    }
    @Test
    void validateAccountDeletion_validAccount_doesNotThrowException() {
        when(existingAccount.getBalance()).thenReturn(BigDecimal.ZERO);
        when(existingAccount.getStatus()).thenReturn(AccountStatus.INACTIVE);

        assertDoesNotThrow(() -> accountValidationService.validateAccountDeletion(existingAccount));
    }
    @Test
    void validateAccountDeletion_nonZeroBalance_throwsAccountException() {
        when(existingAccount.getBalance()).thenReturn(BigDecimal.valueOf(1000));

        AccountException exception = assertThrows(AccountException.class, () -> accountValidationService.validateAccountDeletion(existingAccount));
        assertEquals(AccountErrorCode.INVALID_ACCOUNT_STATUS, exception.getErrorCode());
    }
}
