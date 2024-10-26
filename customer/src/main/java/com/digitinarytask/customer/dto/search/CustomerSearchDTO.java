package com.digitinarytask.customer.dto.search;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSearchDTO {
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid mobile number format")
    private String mobile;

    private String address;

    private String city;

    @Size(max = 100, message = "Country must not exceed 100 characters")
    private String country;

    @Pattern(regexp = "^[0-9]{5,10}$", message = "Invalid postal code format")
    private String postalCode;

    // Pagination fields
    @Min(value = 0, message = "Page number cannot be negative")
    private Integer page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    private Integer size = 10;

    private String sortBy = "id";

    @Pattern(regexp = "^(asc|desc)$", message = "Sort direction must be 'asc' or 'desc'")
    private String sortDirection = "asc";
}
