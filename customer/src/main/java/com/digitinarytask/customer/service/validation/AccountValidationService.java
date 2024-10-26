package com.digitinarytask.customer.service.validation;

import com.digitinarytask.customer.domain.entity.Account;
import com.digitinarytask.customer.domain.enumeration.AccountStatus;
import com.digitinarytask.customer.domain.enumeration.AccountType;
import com.digitinarytask.customer.domain.enumeration.error.AccountErrorCode;
import com.digitinarytask.customer.dto.domain.AccountDTO;
import com.digitinarytask.customer.exception.AccountException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service

public class AccountValidationService {


    /**
     * Validates the id
     */
    public void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new AccountException("Invalid account ID",
                AccountErrorCode.INVALID_ACCOUNT_ID);
        }
    }

    /**
     * Validates new account creation
     */
    public void validateNewAccount(AccountDTO accountDTO) {
        Assert.notNull(accountDTO, "Account data cannot be null");

        validateAccountNumber(accountDTO.getAccountNumber());
        validateAccountName(accountDTO.getAccountName());
        validateInitialBalance(accountDTO.getBalance());
        validateAccountType(accountDTO.getAccountType());
        validateOpeningDate(accountDTO.getDateOpened());
        validateInitialStatus(accountDTO.getStatus());
    }

    /**
     * Validates account update
     */
    public void validateAccountUpdate(Account existingAccount, AccountDTO updateDTO) {
        Assert.notNull(updateDTO, "Update data cannot be null");
        Assert.notNull(existingAccount, "Existing account cannot be null");

        validateId(existingAccount.getId());
        validateAccountStatus(existingAccount.getStatus(), updateDTO.getStatus());
        validateAccountType(updateDTO.getAccountType());
        validateAccountName(updateDTO.getAccountName());

        // Validate if account number is being changed
        if (!existingAccount.getAccountNumber().equals(updateDTO.getAccountNumber())) {
            throw new AccountException("Account number cannot be changed",
                AccountErrorCode.INVALID_ACCOUNT_UPDATE);
        }
    }

    /**
     * Validates balance update
     */
    public void validateBalanceUpdate(BigDecimal currentBalance, BigDecimal amount) {
        Assert.notNull(currentBalance, "Current balance cannot be null");
        Assert.notNull(amount, "Update amount cannot be null");

        BigDecimal newBalance = currentBalance.add(amount);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new AccountException("Insufficient balance for withdrawal",
                AccountErrorCode.INSUFFICIENT_BALANCE);
        }

        if (amount.compareTo(new BigDecimal("1000000")) > 0) {
            throw new AccountException("Transaction amount exceeds maximum limit",
                AccountErrorCode.TRANSACTION_LIMIT_EXCEEDED);
        }
    }

    /**
     * Validates account deletion
     */
    public void validateAccountDeletion(Account account) {
        Assert.notNull(account, "Account cannot be null");

        if (account.getBalance().compareTo(BigDecimal.ZERO) != 0) {
            throw new AccountException("Cannot delete account with non-zero balance",
                AccountErrorCode.INVALID_ACCOUNT_STATUS);
        }

        if (account.getStatus() != AccountStatus.INACTIVE) {
            throw new AccountException("Account must be inactive before deletion",
                AccountErrorCode.INVALID_ACCOUNT_STATUS);
        }
    }

    // Private validation methods
    private void validateAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            throw new AccountException("Account number is required",
                AccountErrorCode.INVALID_ACCOUNT_NUMBER);
        }

        if (!accountNumber.matches("^[A-Z]{2}\\d{8}$")) {
            throw new AccountException("Invalid account number format. Must be 2 letters followed by 8 digits",
                AccountErrorCode.INVALID_ACCOUNT_NUMBER);
        }
    }

    private void validateAccountName(String accountName) {
        if (accountName == null || accountName.trim().isEmpty()) {
            throw new AccountException("Account name is required",
                AccountErrorCode.INVALID_ACCOUNT_DATA);
        }

        if (accountName.length() > 30) {
            throw new AccountException("Account name cannot exceed 30 characters",
                AccountErrorCode.INVALID_ACCOUNT_DATA);
        }
    }

    private void validateInitialBalance(BigDecimal balance) {
        if (balance == null) {
            throw new AccountException("Initial balance is required",
                AccountErrorCode.INVALID_ACCOUNT_DATA);
        }

        if (balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new AccountException("Initial balance cannot be negative",
                AccountErrorCode.INVALID_ACCOUNT_DATA);
        }
    }

    private void validateAccountType(AccountType accountType) {
        if (accountType == null) {
            throw new AccountException("Account type is required",
                AccountErrorCode.INVALID_ACCOUNT_DATA);
        }
    }

    private void validateOpeningDate(LocalDate openingDate) {
        if (openingDate == null) {
            throw new AccountException("Opening date is required",
                AccountErrorCode.INVALID_ACCOUNT_DATA);
        }

        if (openingDate.isAfter(LocalDate.now())) {
            throw new AccountException("Opening date cannot be in the future",
                AccountErrorCode.INVALID_ACCOUNT_DATA);
        }
    }

    private void validateInitialStatus(AccountStatus status) {
        if (status == null) {
            throw new AccountException("Account status is required",
                AccountErrorCode.INVALID_ACCOUNT_DATA);
        }

        if (status == AccountStatus.INACTIVE) {
            throw new AccountException("New accounts cannot be created with INACTIVE status",
                AccountErrorCode.INVALID_ACCOUNT_DATA);
        }
    }

    private void validateAccountStatus(AccountStatus currentStatus, AccountStatus newStatus) {
        if (currentStatus == AccountStatus.INACTIVE && newStatus == AccountStatus.INACTIVE) {
            throw new AccountException("Inactive accounts cannot be modified",
                AccountErrorCode.INVALID_ACCOUNT_STATUS);
        }
    }

    public void validateBalance(int balance) {
        if (balance < 0) {
            throw new AccountException("Balance cannot be negative",
                AccountErrorCode.INVALID_ACCOUNT_DATA);
        }
    }
}
