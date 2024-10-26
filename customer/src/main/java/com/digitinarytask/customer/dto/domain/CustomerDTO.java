package com.digitinarytask.customer.dto.domain;

import com.digitinarytask.customer.domain.entity.Address;
import com.digitinarytask.customer.domain.enumeration.CustomerType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "customerType"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = IndividualCustomerDTO.class, name = "INDIVIDUAL"),
    @JsonSubTypes.Type(value = OrganizationCustomerDTO.class, name = "ORGANIZATION")
})
public class CustomerDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid mobile number, must be a valid phone number, e.g. +1234567890")
    @Size(max = 15, message = "Mobile number must be at most 15 characters")
    private String mobileNo;

    private Set<@Valid AddressDTO> addresses;

    private Set<@Valid AccountDTO> accounts;
}
