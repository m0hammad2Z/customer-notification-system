package com.digitinarytask.customer.controller;

import com.digitinarytask.customer.dto.domain.AddressDTO;
import com.digitinarytask.customer.service.AccountService;
import com.digitinarytask.customer.service.AddressService;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@ExtendWith(MockitoExtension.class)
@WebMvcTest(AddressController.class)
class AddressControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AddressService addressService;

    @MockBean
    private JwtService jwtService;

    private AddressDTO addressDTO;
    private String content = "{\"postalCode\": \"12345\", \"city\": \"Nairobi\", \"country\": \"Kenya\"}";

    @BeforeEach
    void setUp() {
        addressDTO = AddressDTO.builder()
            .postalCode("12345")
            .city("Nairobi")
            .country("Kenya")
            .state("Nairobi")
            .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getCustomerAddressesReturns200WhenAddressesFound() throws Exception {
        when(addressService.findCustomerAddresses(any(Long.class), any(Pageable.class)))
            .thenReturn(Page.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/addresses/customer/{customerId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void addCustomerAddressReturns201WhenAddressCreated() throws Exception {
        when(addressService.addCustomerAddress(any(Long.class), any(AddressDTO.class)))
            .thenReturn(addressDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/addresses/customer/{customerId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.postalCode").value("12345"))
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void updateCustomerAddressReturns200WhenAddressUpdated() throws Exception {
        when(addressService.updateCustomerAddress(any(Long.class), any(Long.class), any(AddressDTO.class)))
            .thenReturn(addressDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/addresses/customer/{customerId}/{addressId}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.postalCode").value("12345"))
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void deleteCustomerAddressReturns204WhenAddressDeleted() throws Exception {
        doNothing().when(addressService).deleteCustomerAddress(any(Long.class));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/addresses/customer/{addressId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isNoContent())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getCustomerAddressesReturns404WhenCustomerNotFound() throws Exception {
        when(addressService.findCustomerAddresses(any(Long.class), any(Pageable.class)))
            .thenThrow(new EntityNotFoundException("Customer not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/addresses/customer/{customerId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void addCustomerAddressReturns400WhenInvalidDataProvided() throws Exception {
        String invalidContent = "{\"postalCode\": \"\", \"city\": \"\", \"country\": \"\"}";

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/addresses/customer/{customerId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidContent)
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isBadRequest())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void updateCustomerAddressReturns404WhenAddressNotFound() throws Exception {
        when(addressService.updateCustomerAddress(any(Long.class), any(Long.class), any(AddressDTO.class)))
            .thenThrow(new EntityNotFoundException("Address not found"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/addresses/customer/{customerId}/{addressId}", 1L, 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void deleteCustomerAddressReturns404WhenAddressNotFound() throws Exception {
        doThrow(new EntityNotFoundException("Address not found")).when(addressService).deleteCustomerAddress(any(Long.class));

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/addresses/customer/{addressId}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andDo(print());
    }
}
