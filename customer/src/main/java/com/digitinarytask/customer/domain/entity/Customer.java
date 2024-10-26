package com.digitinarytask.customer.domain.entity;

import com.digitinarytask.shared.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a customer entity.
 */
@Entity
@Table(name = "customer")
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "customerType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = IndividualCustomer.class, name = "INDIVIDUAL"),
    @JsonSubTypes.Type(value = OrganizationCustomer.class, name = "ORGANIZATION")
})
public class Customer extends BaseEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
    @SequenceGenerator(name = "customer_seq", sequenceName = "customer_seq", allocationSize = 1)
    private Long id;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "mobile_no", length = 15)
    private String mobileNo;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Address> addresses = new HashSet<>();

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Account> accounts = new HashSet<>();

    public void addAccount(Account account) {
        accounts.add(account);
        account.setCustomer(this);
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
        account.setCustomer(null);
    }

    public void addAddress(Address address) {
        addresses.add(address);
        address.setCustomer(this);
    }

    public void removeAddress(Address address) {
        addresses.remove(address);
        address.setCustomer(null);
    }
}
