package com.digitinarytask.customer.dto.search;

import com.digitinarytask.customer.domain.enumeration.AccountStatus;
import com.digitinarytask.customer.domain.enumeration.AccountType;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSearchDTO {
    private String accountNumber;

    private String accountName;

    @DecimalMin(value = "0.0", inclusive = false, message = "Minimum balance must be greater than zero")
    private BigDecimal minBalance;

    @DecimalMin(value = "0.0", inclusive = false, message = "Maximum balance must be greater than zero")
    private BigDecimal maxBalance;

    @PastOrPresent(message = "From date must be in the past or present")
    private LocalDate fromDate;

    @PastOrPresent(message = "To date must be in the past or present")
    private LocalDate toDate;

    private AccountStatus status;

    private AccountType accountType;

    @Min(value = 0, message = "Customer ID must be zero or greater")
    private Long customerId;

    @Min(value = 0, message = "Page number must be zero or greater")
    private Integer page;

    @Min(value = 1, message = "Page size must be at least 1")
    private Integer size;

    private String sortBy = "id";

    @Pattern(regexp = "^(asc|desc)$", message = "Sort direction must be 'asc' or 'desc'")
    private String sortDirection = "asc";
}
