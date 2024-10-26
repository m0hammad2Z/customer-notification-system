package com.digitinarytask.customer.controller;

import com.digitinarytask.customer.dto.domain.AccountDTO;
import com.digitinarytask.customer.dto.search.AccountSearchDTO;
import com.digitinarytask.customer.dto.reposnse.CustomerAccountsSummaryDTO;
import com.digitinarytask.customer.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Account Management", description = "APIs for managing accounts")
@Validated
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Create Account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Account created successfully", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json")),
    })
    @PostMapping("/customer/{customerId}")
    public ResponseEntity<AccountDTO> createAccount(@PathVariable Long customerId, @RequestBody AccountDTO accountDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.createAccount(customerId, accountDTO));
    }

    @Operation(summary = "Update Account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account updated successfully", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(mediaType = "application/json")),
    })
    @PutMapping("/{accountId}")
    public ResponseEntity<AccountDTO> updateAccount(@PathVariable Long accountId, @RequestBody AccountDTO accountDTO) {
        return ResponseEntity.ok(accountService.updateAccount(accountId, accountDTO));
    }

    @Operation(summary = "Delete Account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Account deleted successfully", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(mediaType = "application/json")),
    })
    @DeleteMapping("/{accountId}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long accountId) {
        accountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get Account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account found", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(mediaType = "application/json")),
    })
    @GetMapping("/{accountId}")
    public ResponseEntity<AccountDTO> getAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(accountService.getAccount(accountId));
    }

    @Operation(summary = "Get Account Summary")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account summary found", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Account summary not found", content = @Content(mediaType = "application/json")),
    })
    @GetMapping("customer/{customerId}/summary")
    public ResponseEntity<CustomerAccountsSummaryDTO> getCustomerAccountsSummary(@PathVariable Long customerId) {
        return ResponseEntity.ok(accountService.getCustomerAccountsSummary(customerId));
    }

    @Operation(summary = "Get Top Accounts By Balance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Top accounts found", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Top accounts not found", content = @Content(mediaType = "application/json")),
    })
    @GetMapping("/top/{balance}")
    public ResponseEntity<Page<AccountDTO>> getTopAccountsByBalance(@PathVariable int balance, @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        return ResponseEntity.ok(accountService.findTopAccountsByBalance(balance, pageable));
    }

    @Operation(summary = "Search Accounts")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accounts found", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Accounts not found", content = @Content(mediaType = "application/json")),
    })
    @PostMapping("/search")
    public ResponseEntity<Page<AccountDTO>> searchAccounts(@RequestBody AccountSearchDTO searchDTO) {
        return ResponseEntity.ok(accountService.search(searchDTO));
    }

    @Operation(summary = "Update Account Balance")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Account balance updated", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Account not found", content = @Content(mediaType = "application/json")),
    })
    @PatchMapping("/{accountId}/balance")
    public ResponseEntity<Void> updateBalance(@PathVariable Long accountId, @RequestParam BigDecimal amount) {
        accountService.updateBalance(accountId, amount);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get Customer Accounts", description = "Retrieve accounts of a customer by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Accounts found successfully", content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "404", description = "Customer not found", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{customerId}/accounts")
    public Page<AccountDTO> getCustomerAccounts(@PathVariable Long customerId, @org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        return accountService.getCustomerAccounts(customerId, pageable);

    }
}
