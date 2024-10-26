package com.digitinarytask.customer.controller;

import com.digitinarytask.customer.domain.entity.*;
import com.digitinarytask.customer.domain.enumeration.AccountStatus;
import com.digitinarytask.customer.domain.enumeration.AccountType;
import com.digitinarytask.customer.domain.enumeration.AddressType;
import com.digitinarytask.customer.dto.domain.*;
import com.digitinarytask.customer.dto.search.CustomerSearchDTO;
import com.digitinarytask.customer.service.CustomerService;
import com.digitinarytask.customer.service.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @MockBean
    private JwtService jwtService;

    private String content = "{\"name\": \"John Doe\", \"mobileNo\": \"+1234567890\", \"nationalId\": \"123456789\", \"firstName\": \"John\", \"lastName\": \"Doe\", \"customerType\": \"INDIVIDUAL\" ,\"email\": \"test@test.com\"}";


    private CustomerDTO customer;
    private AddressDTO address;
    private AccountDTO account;

    @BeforeEach
    void setUp() {

        address = AddressDTO.builder()
            .city("New York")
            .country("USA")
            .postalCode("10001")
            .state("NY")
            .addressType(AddressType.HOME)
            .build();

        account = AccountDTO.builder()
            .accountNumber("123456789")
            .accountName("John Doe")
            .status(AccountStatus.ACTIVE)
            .accountType(AccountType.CURRENT)
            .balance(BigDecimal.valueOf(1000))
            .build();

        customer = IndividualCustomerDTO.builder()
            .name("John Doe")
            .firstName("John")
            .lastName("Doe")
            .mobileNo("1234567890")
            .nationalId("123456789")
            .addresses(Collections.singleton(address))
            .accounts(Collections.singleton(account))
            .email("email@test.com")
            .build();

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCustomerReturns201WhenCustomerCreated() throws Exception {
        when(customerService.createCustomer(any(CustomerDTO.class))).thenReturn(customer);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John Doe"))
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCustomerReturns400WhenInvalidDataProvided() throws Exception {
        String invalidContent = "{ \"name\": \"\", \"mobileNo\": \"\", \"nationalId\": \"\", \"firstName\": \"\", \"lastName\": \"\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidContent)
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getCustomerReturns200WhenCustomerFound() throws Exception {
        when(customerService.getCustomer(any(Long.class))).thenReturn(customer);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John Doe"))
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getCustomerReturns404WhenCustomerNotFound() throws Exception {
        when(customerService.getCustomer(any(Long.class))).thenThrow(new EntityNotFoundException("Customer not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/{id}", 9999L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCustomerReturns200WhenCustomerUpdated() throws Exception {
        when(customerService.updateCustomer(any(Long.class), any(CustomerDTO.class))).thenReturn(customer);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customers/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John Doe"))
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCustomerReturns404WhenCustomerNotFound() throws Exception {
        when(customerService.updateCustomer(any(Long.class), any(CustomerDTO.class))).thenThrow(new EntityNotFoundException("Customer not found"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customers/{id}", 9999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCustomerReturns204WhenCustomerDeleted() throws Exception {
        doNothing().when(customerService).deleteCustomer(any(Long.class));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/customers/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCustomerReturns404WhenCustomerNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Customer not found")).when(customerService).deleteCustomer(any(Long.class));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/customers/{id}", 9999L)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void searchCustomersReturns200WhenCustomersFound() throws Exception {
        Page<CustomerDTO> page = new PageImpl<>(Collections.singletonList(customer));
        when(customerService.searchCustomers(any(CustomerSearchDTO.class))).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"page\": 0, \"size\": 10, \"sortBy\": \"name\", \"sortDirection\": \"asc\"}")
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void searchCustomersReturns400WhenInvalidDataProvided() throws Exception {
        String invalidContent = "{ \"page\": -1, \"size\": 0, \"sortBy\": \"\", \"sortDirection\": \"\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidContent)
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllCustomersReturns200WhenNoCustomersFound() throws Exception {
        when(customerService.getAllCustomers(any(Pageable.class))).thenReturn(Page.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getCustomerByTypeReturns200WhenCustomersFound() throws Exception {
        Page<CustomerDTO> page = new PageImpl<>(Collections.singletonList(customer));
        when(customerService.getCustomersByType(any(String.class), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/type/{type}", "INDIVIDUAL")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(print());
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateCustomerReturns400WhenInvalidDataProvided() throws Exception {
        String invalidContent = "{ \"name\": \"\", \"mobileNo\": \"\", \"nationalId\": \"\", \"firstName\": \"\", \"lastName\": \"\" }";

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/customers/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidContent)
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void searchCustomersReturns200WhenNoCustomersFound() throws Exception {
        when(customerService.searchCustomers(any(CustomerSearchDTO.class))).thenReturn(Page.empty());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/customers/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"page\": 0, \"size\": 10, \"sortBy\": \"name\", \"sortDirection\": \"asc\"}")
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(print());
    }
}
