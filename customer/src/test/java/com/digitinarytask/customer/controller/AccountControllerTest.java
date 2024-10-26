package com.digitinarytask.customer.controller;

import com.digitinarytask.customer.domain.enumeration.AccountStatus;
import com.digitinarytask.customer.domain.enumeration.AccountType;
import com.digitinarytask.customer.dto.domain.AccountDTO;
import com.digitinarytask.customer.dto.search.AccountSearchDTO;
import com.digitinarytask.customer.dto.reposnse.CustomerAccountsSummaryDTO;
import com.digitinarytask.customer.service.AccountService;
import com.digitinarytask.customer.service.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AccountController.class)
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private JwtService jwtService;

    private AccountDTO accountDTO;
    private String content = "{\"accountNumber\": \"12345678\", \"accountName\": \"John Doe\", \"balance\": \"1000.00\", \"accountType\": \"CURRENT\"}";

    @BeforeEach
    void setUp() {
        accountDTO = AccountDTO.builder()
            .accountNumber("123456789")
            .accountName("John Doe")
            .status(AccountStatus.ACTIVE)
            .accountType(AccountType.CURRENT)
            .balance(BigDecimal.valueOf(1000))
            .build();

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createAccountReturns201WhenSuccessful() throws Exception {
        when(accountService.createAccount(anyLong(), any(AccountDTO.class))).thenReturn(accountDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/customer/1")

                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.accountNumber").value("123456789"))
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateAccountReturns200WhenAccountIsUpdated() throws Exception {
        when(accountService.updateAccount(anyLong(), any(AccountDTO.class))).thenReturn(accountDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/accounts/1")

                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.accountNumber").value("123456789"))
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateAccountReturns404WhenAccountNotFound() throws Exception {
        when(accountService.updateAccount(anyLong(), any(AccountDTO.class))).thenThrow(new EntityNotFoundException("Account not found"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/accounts/1")

                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteAccountReturns204WhenAccountDeleted() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/accounts/1")

                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAccountReturns200WhenAccountFound() throws Exception {
        when(accountService.getAccount(anyLong())).thenReturn(accountDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/1")

                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.accountNumber").value("123456789"))
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getCustomerAccountsSummaryReturns200WhenFound() throws Exception {
        CustomerAccountsSummaryDTO summaryDTO = new CustomerAccountsSummaryDTO();
        when(accountService.getCustomerAccountsSummary(anyLong())).thenReturn(summaryDTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/customer/1/summary")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getTopAccountsByBalanceReturns200WhenAccountsFound() throws Exception {
        Page<AccountDTO> page = new PageImpl<>(Collections.singletonList(accountDTO));
        when(accountService.findTopAccountsByBalance((int) anyLong(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/top/1")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void searchAccountsReturns200WhenAccountsFound() throws Exception {
        Page<AccountDTO> page = new PageImpl<>(Collections.singletonList(accountDTO));
        when(accountService.search(any(AccountSearchDTO.class))).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/accounts/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"page\": 0, \"size\": 10, \"sortBy\": \"accountNumber\", \"sortDirection\": \"ASC\"}")
            .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateBalanceReturns200WhenBalanceUpdated() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/accounts/1/balance")
                .param("amount", "500.00")
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getCustomerAccountsReturns200WhenAccountsFound() throws Exception {
        Page<AccountDTO> page = new PageImpl<>(Collections.singletonList(accountDTO));
        when(accountService.getCustomerAccounts(anyLong(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/accounts/1/accounts")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(print());
    }
}
