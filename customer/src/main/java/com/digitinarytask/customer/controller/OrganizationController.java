package com.digitinarytask.customer.controller;

import com.digitinarytask.customer.dto.domain.OrganizationDTO;
import com.digitinarytask.customer.dto.search.OrganizationSearchDTO;
import com.digitinarytask.customer.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/organizations")
@RequiredArgsConstructor
@Tag(name = "Organization Management", description = "APIs for managing organizations")
@Validated
public class OrganizationController {

    private final OrganizationService organizationService;

    @Operation(summary = "Update organization", description = "Update an organization by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Organization updated successfully", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Organization not found", content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/{customerId}")
    public ResponseEntity<OrganizationDTO> updateOrganization(@PathVariable Long customerId, @Valid @RequestBody OrganizationDTO organizationDTO) {
        OrganizationDTO result = organizationService.updateOrganization(customerId, organizationDTO);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Search organizations", description = "Search organizations with various criteria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Organizations found successfully", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/search")
    public ResponseEntity<Page<OrganizationDTO>> searchOrganizations(@Valid @RequestBody OrganizationSearchDTO searchDTO) {
        Page<OrganizationDTO> result = organizationService.searchOrganizations(searchDTO);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get organization by ID", description = "Retrieve an organization by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Organization found successfully", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Organization not found", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{customerId}")
    public ResponseEntity<OrganizationDTO> getOrganization(@PathVariable Long customerId) {
        OrganizationDTO organization = organizationService.getOrganization(customerId);
        return ResponseEntity.ok(organization);
    }

    @Operation(summary = "Get all organizations", description = "Retrieve all organizations")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Organizations found successfully", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("")
    public ResponseEntity<Page<OrganizationDTO>> getAllOrganizations(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        Page<OrganizationDTO> result = organizationService.getOrganizations(pageable);
        return ResponseEntity.ok(result);
    }
}
