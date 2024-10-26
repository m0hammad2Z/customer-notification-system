package com.digitinarytask.customer.dto.search;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationSearchDTO {

    private String registrationNumber;
    private String legalName;
    private String trademarkName;

    @Min(value = 0, message = "Page must be greater than or equal to 0")
    @Builder.Default
    private Integer page = 0;

    @Min(value = 1, message = "Page size must be at least 1")
    @Builder.Default
    private Integer size = 10;

    private String sortBy = "id";

    @Builder.Default
    @Pattern(regexp = "^(asc|desc)$", message = "Sort direction must be 'asc' or 'desc'")
    private String sortDirection = "asc";
}
