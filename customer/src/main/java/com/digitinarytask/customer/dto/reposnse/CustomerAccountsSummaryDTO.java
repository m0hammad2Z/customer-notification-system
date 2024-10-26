package com.digitinarytask.customer.dto.reposnse;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerAccountsSummaryDTO {
    private Long totalAccounts;
    private BigDecimal totalBalance;
    private BigDecimal averageBalance;
    private Long activeAccounts;
    private Long inactiveAccounts;
}
