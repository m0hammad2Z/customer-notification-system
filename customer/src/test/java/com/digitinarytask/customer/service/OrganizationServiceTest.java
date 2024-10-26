package com.digitinarytask.customer.service;

import com.digitinarytask.customer.domain.entity.Organization;
import com.digitinarytask.customer.dto.domain.OrganizationDTO;
import com.digitinarytask.customer.dto.mapper.OrganizationMapper;
import com.digitinarytask.customer.dto.search.OrganizationSearchDTO;
import com.digitinarytask.customer.domain.enumeration.error.OrganizationErrorCode;
import com.digitinarytask.customer.exception.OrganizationException;
import com.digitinarytask.customer.repository.OrganizationRepository;
import com.digitinarytask.customer.service.validation.OrganizationValidationService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;
    @Mock
    private OrganizationMapper organizationMapper;
    @Mock
    private OrganizationValidationService validator;

    @InjectMocks
    private OrganizationService organizationService;

    private OrganizationDTO testOrganizationDTO;
    private Organization testOrganization;
    private OrganizationSearchDTO testSearchDTO;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        testOrganizationDTO = OrganizationDTO.builder()
            .id(1L)
            .registrationNumber("REG123")
            .legalName("Sample Legal Name")
            .trademarkName("Sample Trademark")
            .build();

        testOrganization = new Organization();
        testOrganization.setId(1L);
        testOrganization.setRegistrationNumber("REG123");

        testSearchDTO = new OrganizationSearchDTO();
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void updateOrganization_Success() {
        when(organizationRepository.findByCustomerId(1L)).thenReturn(Optional.of(testOrganization));
        doNothing().when(validator).validateOrganizationUpdate(testOrganization, testOrganizationDTO);
        when(organizationMapper.toEntity(testOrganizationDTO)).thenReturn(testOrganization);
        when(organizationRepository.save(testOrganization)).thenReturn(testOrganization);
        when(organizationMapper.toDTO(testOrganization)).thenReturn(testOrganizationDTO);

        OrganizationDTO result = organizationService.updateOrganization(1L, testOrganizationDTO);

        assertNotNull(result);
        assertEquals(testOrganizationDTO.getRegistrationNumber(), result.getRegistrationNumber());
    }

    @Test
    void updateOrganization_OrganizationNotFound() {
        when(organizationRepository.findByCustomerId(1L)).thenReturn(Optional.empty());

        OrganizationException exception = assertThrows(OrganizationException.class, () -> organizationService.updateOrganization(1L, testOrganizationDTO));
        assertEquals(OrganizationErrorCode.ORG_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void updateOrganization_ErrorDuringUpdate() {
        when(organizationRepository.findByCustomerId(1L)).thenReturn(Optional.of(testOrganization));
        doNothing().when(validator).validateOrganizationUpdate(testOrganization, testOrganizationDTO);
        when(organizationMapper.toEntity(testOrganizationDTO)).thenReturn(testOrganization);
        when(organizationRepository.save(testOrganization)).thenThrow(new RuntimeException("Database error"));

        OrganizationException exception = assertThrows(OrganizationException.class, () -> organizationService.updateOrganization(1L, testOrganizationDTO));
        assertEquals(OrganizationErrorCode.UPDATE_ORG_FAILED, exception.getErrorCode());
    }


    @Test
    void getOrganization_Success() {
        when(organizationRepository.findByCustomerId(1L)).thenReturn(Optional.of(testOrganization));
        when(organizationMapper.toDTO(testOrganization)).thenReturn(testOrganizationDTO);

        OrganizationDTO result = organizationService.getOrganization(1L);

        assertNotNull(result);
        assertEquals(testOrganizationDTO.getRegistrationNumber(), result.getRegistrationNumber());
    }

    @Test
    void getOrganization_OrganizationNotFound() {
        when(organizationRepository.findByCustomerId(1L)).thenReturn(Optional.empty());

        OrganizationException exception = assertThrows(OrganizationException.class, () -> organizationService.getOrganization(1L));
        assertEquals(OrganizationErrorCode.ORG_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void searchOrganizations_Success() {
        Page<Organization> organizationPage = new PageImpl<>(Collections.singletonList(testOrganization));
        when(organizationRepository.searchOrganizations(testSearchDTO)).thenReturn(organizationPage);
        when(organizationMapper.toDTO(testOrganization)).thenReturn(testOrganizationDTO);

        Page<OrganizationDTO> result = organizationService.searchOrganizations(testSearchDTO);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testOrganizationDTO.getRegistrationNumber(), result.getContent().get(0).getRegistrationNumber());
    }

    @Test
    void searchOrganizations_ErrorDuringSearch() {
        when(organizationRepository.searchOrganizations(testSearchDTO)).thenThrow(new RuntimeException("Database error"));

        OrganizationException exception = assertThrows(OrganizationException.class, () -> organizationService.searchOrganizations(testSearchDTO));
        assertEquals(OrganizationErrorCode.SEARCH_ORG_FAILED, exception.getErrorCode());
    }

    @Test
    void getOrganizations_Success() {
        Page<Organization> organizationPage = new PageImpl<>(Collections.singletonList(testOrganization));
        when(organizationRepository.findAll(pageable)).thenReturn(organizationPage);
        when(organizationMapper.toDTO(testOrganization)).thenReturn(testOrganizationDTO);

        Page<OrganizationDTO> result = organizationService.getOrganizations(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testOrganizationDTO.getRegistrationNumber(), result.getContent().get(0).getRegistrationNumber());
    }

    @Test
    void getOrganizations_ErrorDuringFetch() {
        when(organizationRepository.findAll(pageable)).thenThrow(new RuntimeException("Database error"));

        OrganizationException exception = assertThrows(OrganizationException.class, () -> organizationService.getOrganizations(pageable));
        assertEquals(OrganizationErrorCode.FETCH_ORG_FAILED, exception.getErrorCode());
    }
}
