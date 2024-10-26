package com.digitinarytask.customer.dto.domain;

import com.digitinarytask.customer.domain.enumeration.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {

    private Long id;

    @NotBlank(message = "Postal code is required")
    @Size(max = 10, min = 5, message = "Postal code must be between 5 and 10 characters")
    @Pattern(regexp = "\\d{5}(-\\d{4})?", message = "Invalid postal code format. Must be 5 digits or 5+4 format, e.g. 12345 or 12345-6789")
    private String postalCode;

    @NotBlank(message = "City is required")
    @Size(max = 50, message = "City must be less than 50 characters")
    @Pattern(regexp = "^([a-zA-Z]+|[a-zA-Z]+\\s[a-zA-Z]+)$", message = "City name contains invalid characters, only alphabets and spaces are allowed, e.g. New York")
    private String city;

    @Size(max = 50, message = "State must be less than 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "State name contains invalid characters, only alphabets, spaces and hyphens are allowed, e.g. New York")
    private String state;

    @NotBlank(message = "Country is required")
    @Size(max = 50, message = "Country must be less than 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s-]+$", message = "Country name contains invalid characters, only alphabets, spaces and hyphens are allowed, e.g. United States")
    private String country;

    private AddressType addressType;
}
