package com.example.real_estate_project.service;

import com.example.real_estate_project.model.Property;
import com.example.real_estate_project.model.User;
import com.example.real_estate_project.repository.PropertyRepository;
import com.example.real_estate_project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MultipartFile imageFile;

    @InjectMocks
    private PropertyService propertyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --------------------------------------------------
    // ✅ TEST: savePropertyWithImage()
    // --------------------------------------------------
    @Test
    void testSavePropertyWithImage_Success() throws IOException {
        Property property = new Property();
        property.setTitle("Test House");
        property.setOwner(new User());

        when(imageFile.getOriginalFilename()).thenReturn("house.jpg");
        doNothing().when(imageFile).transferTo(any(File.class));

        propertyService.savePropertyWithImage(property, imageFile);

        verify(userRepository, times(1)).save(property.getOwner());
        verify(propertyRepository, times(1)).save(property);
        assertNotNull(property.getImageUrl());
        assertTrue(property.getImageUrl().contains("/uploads/"));
    }

    // --------------------------------------------------
    // ✅ TEST: saveProperty()
    // --------------------------------------------------
    @Test
    void testSaveProperty_ShouldSaveOwnerAndProperty() {
        Property property = new Property();
        User owner = new User();
        property.setOwner(owner);

        propertyService.saveProperty(property);

        verify(userRepository, times(1)).save(owner);
        verify(propertyRepository, times(1)).save(property);
    }

    // --------------------------------------------------
    // ✅ TEST: getPendingProperties()
    // --------------------------------------------------
    @Test
    void testGetPendingProperties() {
        Property p1 = new Property();
        p1.setStatus("PENDING");
        when(propertyRepository.findByStatus("PENDING")).thenReturn(Collections.singletonList(p1));

        List<Property> result = propertyService.getPendingProperties();

        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getStatus());
    }

    // --------------------------------------------------
    // ✅ TEST: getAllApprovedProperties()
    // --------------------------------------------------
    @Test
    void testGetAllApprovedProperties() {
        Property p1 = new Property();
        p1.setStatus("APPROVED");
        when(propertyRepository.findByStatus("APPROVED")).thenReturn(List.of(p1));

        List<Property> result = propertyService.getAllApprovedProperties();

        assertEquals(1, result.size());
        assertEquals("APPROVED", result.get(0).getStatus());
    }

    // --------------------------------------------------
    // ✅ TEST: getById()
    // --------------------------------------------------
    @Test
    void testGetById_Found() {
        Property property = new Property();
        property.setId(1L);
        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

        Property result = propertyService.getById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetById_NotFound() {
        when(propertyRepository.findById(2L)).thenReturn(Optional.empty());
        Property result = propertyService.getById(2L);
        assertNull(result);
    }

    // --------------------------------------------------
    // ✅ TEST: updateStatus()
    // --------------------------------------------------
    @Test
    void testUpdateStatus_Success() {
        Property property = new Property();
        property.setId(1L);
        property.setStatus("PENDING");

        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

        propertyService.updatePropertyStatus(1L, "APPROVED");

        verify(propertyRepository).save(property);
        assertEquals("APPROVED", property.getStatus());
    }

    @Test
    void testUpdateStatus_PropertyNotFound() {
        when(propertyRepository.findById(anyLong())).thenReturn(Optional.empty());

        boolean result = propertyService.updatePropertyStatus(100L, "APPROVED");

        assertFalse(result);  // ✅ Expect false, not exception
        verify(propertyRepository, never()).save(any());
    }


    // --------------------------------------------------
    // ✅ TEST: approveProperty()
    // --------------------------------------------------
    @Test
    void testApproveProperty_WhenFound() {
        Property property = new Property();
        property.setId(1L);
        property.setStatus("PENDING");

        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));

        propertyService.approveProperty(1L);

        verify(propertyRepository).save(property);
        assertEquals("APPROVED", property.getStatus());
    }

    @Test
    void testApproveProperty_WhenNotFound() {
        when(propertyRepository.findById(anyLong())).thenReturn(Optional.empty());
        propertyService.approveProperty(10L);
        verify(propertyRepository, never()).save(any());
    }

    // --------------------------------------------------
    // ✅ TEST: getAllProperties()
    // --------------------------------------------------
    @Test
    void testGetAllProperties() {
        when(propertyRepository.findAll()).thenReturn(List.of(new Property(), new Property()));
        List<Property> result = propertyService.getAllProperties();
        assertEquals(2, result.size());
    }

    // --------------------------------------------------
    // ✅ TEST: deleteProperty()
    // --------------------------------------------------
    @Test
    void testDeleteProperty() {
        propertyService.deleteProperty(1L);
        verify(propertyRepository, times(1)).deleteById(1L);
    }

    // --------------------------------------------------
    // ✅ TEST: countAll()
    // --------------------------------------------------
    @Test
    void testCountAll() {
        when(propertyRepository.count()).thenReturn(5L);
        assertEquals(5L, propertyService.countAll());
    }

    // --------------------------------------------------
    // ✅ TEST: searchProperties()
    // --------------------------------------------------
    @Test
    void testSearchProperties() {
        when(propertyRepository.findByFilters("Chennai", 50000.0, "Apartment"))
                .thenReturn(List.of(new Property()));

        List<Property> result = propertyService.searchProperties("Chennai", 50000.0, "Apartment");

        assertEquals(1, result.size());
    }
}
