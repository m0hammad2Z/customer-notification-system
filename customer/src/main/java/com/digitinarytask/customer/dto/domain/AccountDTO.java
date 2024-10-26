package com.digitinarytask.customer.dto.domain;

import com.digitinarytask.customer.domain.enumeration.AccountStatus;
import com.digitinarytask.customer.domain.enumeration.AccountType;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {

    private Long id;

    @NotBlank(message = "Account number is required")
    @Size(max = 30, message = "Account number must be less than 30 characters")
    private String accountNumber;

    @NotBlank
    @Size(max = 30, message = "Account name must be less than 30 characters")
    private String accountName;

    @NotNull(message = "Balance is required")
    @Digits(integer = 10, fraction = 2, message = "Invalid balance format")
    private BigDecimal balance;

    private LocalDate dateOpened = LocalDate.now();

    private AccountStatus status = AccountStatus.ACTIVE;

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    private Long customerId;
}
