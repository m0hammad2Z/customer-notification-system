package com.digitinarytask.customer.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IndividualCustomer extends Customer {

    @Column(name = "nationalId", length = 10)
    private String nationalId;

    @Column(name = "first_name", length = 100)
    @NotBlank
    private String firstName;

    @Column(name = "last_name", length = 100)
    @NotBlank
    private String lastName;

    @Column(name = "email", length = 100)
    private String email;

}
