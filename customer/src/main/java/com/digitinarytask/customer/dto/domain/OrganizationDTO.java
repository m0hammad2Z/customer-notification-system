package com.digitinarytask.customer.dto.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationDTO {

    private Long id;

    @NotBlank(message = "Registration number is required")
    @Size(max = 100, message = "Registration number must be at most 100 characters")
    private String registrationNumber;

    @NotBlank(message = "Legal name is required")
    @Size(max = 100, message = "Legal name must be at most 100 characters")
    private String legalName;

    @NotBlank(message = "Trademark name is required")
    @Size(max = 100, message = "Trademark name must be at most 100 characters")
    private String trademarkName;
}
