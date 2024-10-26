package com.digitinarytask.customer.dto.domain;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationCustomerDTO extends CustomerDTO {

    private @Valid OrganizationDTO organization;
}
