package com.digitinarytask.customer.domain.entity;

import com.digitinarytask.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

/**
 * Represents a customer organization entity.
 */

@Entity
@Table(name = "customer_org")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Organization extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_org_seq")
    @SequenceGenerator(name = "customer_org_seq", sequenceName = "customer_org_seq", allocationSize = 1)
    private Long id;

    @Column(name = "registration_no", length = 100, nullable = false)
    private String registrationNumber;

    @Column(name = "legal_name", length = 100, nullable = false)
    private String legalName;

    @Column(name = "trademark_name", length = 100, nullable = false)
    private String trademarkName;

    @OneToOne(mappedBy = "organization", fetch = FetchType.LAZY)
    @ToString.Exclude
    private OrganizationCustomer organizationCustomer;
}
