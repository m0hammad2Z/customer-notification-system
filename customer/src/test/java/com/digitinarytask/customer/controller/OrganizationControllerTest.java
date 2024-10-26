package com.digitinarytask.customer.controller;

import com.digitinarytask.customer.dto.domain.OrganizationDTO;
import com.digitinarytask.customer.dto.search.OrganizationSearchDTO;
import com.digitinarytask.customer.service.OrganizationService;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(OrganizationController.class)
@ActiveProfiles("test")  // Uses application-test.properties if available
public class OrganizationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrganizationService organizationService;

    @MockBean
    private JwtService jwtService;

    private OrganizationDTO organization;

    @BeforeEach
    void setUp() {
        organization = OrganizationDTO.builder()
            .legalName("Tech Corp")
            .registrationNumber("AS22123456")
            .trademarkName("Tech")
            .build();

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateOrganizationReturns200WhenOrganizationIsUpdated() throws Exception {
        when(organizationService.updateOrganization(any(Long.class), any(OrganizationDTO.class)))
            .thenReturn(organization);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/organizations/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"legalName\": \"Tech Corp\", \"registrationNumber\": \"AS22123456\", \"trademarkName\": \"Tech\" }")
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.legalName").value("Tech Corp"))
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void updateOrganizationReturns404WhenOrganizationNotFound() throws Exception {
        when(organizationService.updateOrganization(any(Long.class), any(OrganizationDTO.class)))
            .thenThrow(new EntityNotFoundException("Organization not found"));

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/organizations/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"legalName\": \"Tech Corp\", \"registrationNumber\": \"AS22123456\", \"trademarkName\": \"Tech\" }")
                .with(csrf()))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void searchOrganizationsReturns200WhenOrganizationsFound() throws Exception {
        when(organizationService.searchOrganizations(any(OrganizationSearchDTO.class)))
            .thenReturn(Page.empty());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/organizations/search")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"page\": 0, \"size\": 10, \"sortBy\": \"legalName\", \"sortDirection\": \"asc\" }")
                .with(csrf()))  // Include CSRF token
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getOrganizationReturns200WhenOrganizationFound() throws Exception {
        when(organizationService.getOrganization(any(Long.class)))
            .thenReturn(organization);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/organizations/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.legalName").value("Tech Corp"))
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getOrganizationReturns404WhenOrganizationNotFound() throws Exception {
        when(organizationService.getOrganization(any(Long.class)))
            .thenThrow(new EntityNotFoundException("Organization not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/organizations/{id}", 9999L)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound())
            .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"USER", "ADMIN"})
    void getAllOrganizationsReturns200WhenOrganizationsFound() throws Exception {
        when(organizationService.getOrganizations(any(Pageable.class)))
            .thenReturn(Page.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/organizations")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andDo(print());
    }
}
