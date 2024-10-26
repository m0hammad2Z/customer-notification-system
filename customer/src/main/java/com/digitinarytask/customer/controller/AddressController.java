package com.digitinarytask.customer.controller;

import com.digitinarytask.customer.dto.domain.AddressDTO;
import com.digitinarytask.customer.service.AddressService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
@Tag(name = "Address Management", description = "APIs for managing addresses")
@Validated
public class AddressController {

    private final AddressService addressService;

    // Get all addresses for a customer
    @GetMapping("customer/{customerId}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Addresses found successfully")
    })
    public ResponseEntity<Page<AddressDTO>> getCustomerAddresses(@PathVariable Long customerId, @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        Page<AddressDTO> result = addressService.findCustomerAddresses(customerId, pageable);
        return ResponseEntity.ok(result);
    }

    // Add a new address for a customer
    @PostMapping("customer/{customerId}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Address created successfully")
    })
    public ResponseEntity<AddressDTO> addCustomerAddress(@PathVariable Long customerId, @Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO result = addressService.addCustomerAddress(customerId, addressDTO);
        return ResponseEntity.status(201).body(result);
    }

    // Update an address for a customer
    @PutMapping("customer/{customerId}/{addressId}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Address updated successfully")
    })
    public ResponseEntity<AddressDTO> updateCustomerAddress(@PathVariable Long customerId, @PathVariable Long addressId, @Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO result = addressService.updateCustomerAddress(customerId, addressId, addressDTO);
        return ResponseEntity.ok(result);
    }

    // Delete an address for a customer
    @DeleteMapping("customer/{addressId}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Address deleted successfully")
    })
    public ResponseEntity<Void> deleteCustomerAddress( @PathVariable Long addressId) {
        addressService.deleteCustomerAddress(addressId);
        return ResponseEntity.noContent().build();
    }
}
