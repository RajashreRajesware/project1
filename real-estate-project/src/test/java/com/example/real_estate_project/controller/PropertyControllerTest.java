package com.example.real_estate_project.controller;

import com.example.real_estate_project.model.Property;
import com.example.real_estate_project.model.User;
import com.example.real_estate_project.repository.UserRepository;
import com.example.real_estate_project.service.ImageUploadService;
import com.example.real_estate_project.service.PropertyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WithMockUser(username = "user@example.com", roles = "USER")  // ✅ Added to simulate logged-in user
@WebMvcTest(PropertyController.class)
@AutoConfigureMockMvc(addFilters = true)
class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PropertyService propertyService;

    @MockBean
    private ImageUploadService imageUploadService;

    @MockBean
    private UserRepository userRepository;

    private User mockUser;
    private Property mockProperty;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("user@example.com");
        mockUser.setFullName("John Doe");

        mockProperty = new Property();
        mockProperty.setId(1L);
        mockProperty.setTitle("Luxury Villa");
        mockProperty.setOwner(mockUser);
        mockProperty.setStatus("PENDING");
    }

    @Test
    void testListPropertiesWithoutFilters() throws Exception {
        when(propertyService.getAllProperties()).thenReturn(Collections.singletonList(mockProperty));

        mockMvc.perform(get("/properties/list"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("properties"))
                .andExpect(view().name("property-list"));

        verify(propertyService, times(1)).getAllProperties();
    }

    @Test
    void testListPropertiesWithFilters() throws Exception {
        when(propertyService.searchProperties("Chennai", 500000.0, "Apartment"))
                .thenReturn(Collections.singletonList(mockProperty));

        mockMvc.perform(get("/properties/list")
                        .param("location", "Chennai")
                        .param("price", "500000")
                        .param("type", "Apartment"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("properties"))
                .andExpect(view().name("property-list"));

        verify(propertyService).searchProperties("Chennai", 500000.0, "Apartment");
    }

    @Test
    void testViewPendingProperties() throws Exception {
        when(propertyService.getPendingProperties()).thenReturn(Collections.singletonList(mockProperty));

        mockMvc.perform(get("/properties/pending"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("properties"))
                .andExpect(view().name("property-list"));
    }

    @Test
    void testPropertyForm() throws Exception {
        mockMvc.perform(get("/properties/form"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("property"))
                .andExpect(view().name("property-form"));
    }

    @Test
    void testShowAddPropertyForm() throws Exception {
        mockMvc.perform(get("/properties/add"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("property"))
                .andExpect(view().name("add-property"));
    }

    @Test
    void testSavePropertyWithImage() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "imageFile", "test.jpg", "image/jpeg", "fake image".getBytes()
        );

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser));
        when(imageUploadService.uploadImage(any())).thenReturn("uploaded.jpg");

        mockMvc.perform(multipart("/properties/save")
                        .file(mockFile)
                        .param("title", "New Property")
                        .param("description", "Nice home")
                        .param("price", "12345")
                        .param("type", "House")
                        .param("location", "Chennai")
                        .with(user("user@example.com").roles("USER"))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/properties/list"));

        verify(propertyService).saveProperty(any(Property.class));
    }

    @Test
    void testViewProperty() throws Exception {
        when(propertyService.getById(1L)).thenReturn(mockProperty);

        mockMvc.perform(get("/properties/view/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("property"))
                .andExpect(view().name("view-property"));
    }

    @Test
    void testEditPropertyExists() throws Exception {
        when(propertyService.getById(1L)).thenReturn(mockProperty);

        mockMvc.perform(get("/properties/edit/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("property"))
                .andExpect(view().name("property-form"));
    }

    @Test
    void testEditPropertyNotExists() throws Exception {
        when(propertyService.getById(99L)).thenReturn(null);

        mockMvc.perform(get("/properties/edit/99"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/properties/list"));
    }

    @Test
    void testUpdateProperty() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile(
                "imageFile", "new.jpg", "image/jpeg", "image".getBytes()
        );

        Property existing = new Property();
        existing.setId(1L);
        existing.setOwner(new User());

        when(propertyService.getById(1L)).thenReturn(existing);
        when(imageUploadService.uploadImage(any())).thenReturn("updated.jpg");
        when(userRepository.save(any(User.class))).thenReturn(existing.getOwner()); // ✅ mock save

        mockMvc.perform(multipart("/properties/update")
                        .file(mockFile)
                        .param("id", "1")
                        .param("title", "Updated Villa")
                        .param("description", "Better home")
                        .param("price", "99999")
                        .param("type", "Apartment")
                        .param("location", "Chennai")
                        .param("owner.fullName", "John Doe")
                        .param("owner.email", "john@example.com")
                        .with(user("user@example.com").roles("USER")) // ✅ simulate authenticated user
                        .with(csrf())) // ✅ include CSRF token for POST
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/properties/list"));

        verify(propertyService).saveProperty(any(Property.class));
    }

}
