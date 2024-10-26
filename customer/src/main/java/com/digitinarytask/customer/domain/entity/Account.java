package com.digitinarytask.customer.domain.entity;

import com.digitinarytask.customer.domain.enumeration.AccountStatus;
import com.digitinarytask.customer.domain.enumeration.AccountType;
import com.digitinarytask.shared.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Represents an account entity.
 */
@Entity
@Table(name = "account")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Account extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_seq")
    @SequenceGenerator(name = "account_seq", sequenceName = "account_seq", allocationSize = 1)
    private Long id;

    @NotNull
    @Size(max = 30)
    @Column(name = "account_number", length = 30, nullable = false)
    private String accountNumber;

    @NotNull
    @Size(max = 30)
    @Column(name = "account_name", length = 30, nullable = false)
    private String accountName;

    @Column(name = "balance", precision = 15, scale = 2, nullable = false)
    private BigDecimal balance;

    @Column(name = "date_opened", updatable = false)
    private LocalDate dateOpened = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull
    private AccountStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_type")
    @NotNull
    private AccountType accountType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private Customer customer;
}
