package com.digitinarytask.customer.controller;

import com.digitinarytask.customer.dto.domain.CustomerDTO;
import com.digitinarytask.customer.dto.search.CustomerSearchDTO;
import com.digitinarytask.customer.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "APIs for managing customers")
@Validated
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Create Customer", description = "Create a new customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Customer created successfully", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json"))
    })
    @PostMapping
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);
        return ResponseEntity.status(201).body(createdCustomer);
    }

    @Operation(summary = "Get Customer by ID", description = "Retrieve a customer by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer found successfully", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDTO> getCustomer(@PathVariable Long id) {
        CustomerDTO customer = customerService.getCustomer(id);
        return ResponseEntity.ok(customer);
    }

    @Operation(summary = "Update Customer", description = "Update an existing customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer updated successfully", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(mediaType = "application/json"))
    })
    @PutMapping("/{id}")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @Valid @RequestBody CustomerDTO customerDTO) {
        CustomerDTO updatedCustomer = customerService.updateCustomer(id, customerDTO);
        return ResponseEntity.ok(updatedCustomer);
    }

    @Operation(summary = "Delete Customer", description = "Delete a customer by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Customer deleted successfully", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Search Customers", description = "Search customers with various criteria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customers found successfully", content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/search")
    public ResponseEntity<Page<CustomerDTO>> searchCustomers(@Valid @RequestBody CustomerSearchDTO searchDTO) {
        Page<CustomerDTO> result = customerService.searchCustomers(searchDTO);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get All Customers", description = "Retrieve all customers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customers found successfully", content = @Content(mediaType = "application/json"))
    })
    @GetMapping
    public ResponseEntity<Page<CustomerDTO>> getAllCustomers(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        Page<CustomerDTO> result = customerService.getAllCustomers(pageable);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Get Customer by Type", description = "Retrieve customers by type")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customers found successfully", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/type/{type}")
    public ResponseEntity<Page<CustomerDTO>> getCustomerByType(@PathVariable String type, @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        Page<CustomerDTO> customers = customerService.getCustomersByType(type, pageable);
        return ResponseEntity.ok(customers);
    }
}
