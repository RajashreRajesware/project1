package com.example.real_estate_project.controller;

import com.example.real_estate_project.model.Property;
import com.example.real_estate_project.model.User;
import com.example.real_estate_project.service.PropertyService;
import com.example.real_estate_project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PropertyService propertyService;

    @MockBean
    private UserService userService;

    @MockBean
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testShowDashboard() throws Exception {
        List<Property> mockProperties = Arrays.asList(new Property(), new Property());
        List<User> mockUsers = Arrays.asList(new User(), new User(), new User());

        when(propertyService.getAllProperties()).thenReturn(mockProperties);
        when(userService.getAllUsers()).thenReturn(mockUsers);
        when(authentication.getName()).thenReturn("admin@example.com");

        mockMvc.perform(get("/admin/dashboard").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("propertyCount"))
                .andExpect(model().attributeExists("userCount"))
                .andExpect(model().attribute("userEmail", "admin@example.com"))
                .andExpect(view().name("admin-dashboard"));
    }


    @Test
    void testViewUsers() throws Exception {
        List<User> users = Collections.singletonList(new User());
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("users"))
                .andExpect(view().name("admin-users"));
    }


    @Test
    void testEditUserForm() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFullName("John Doe");

        when(userService.getById(1L)).thenReturn(mockUser);

        mockMvc.perform(get("/admin/users/edit/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("edit-user"));
    }


    @Test
    void testUpdateUser() throws Exception {
        mockMvc.perform(post("/admin/users/update")
                        .param("id", "1")
                        .param("fullName", "Jane Doe")
                        .param("email", "jane@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService, times(1)).updateUser(any(User.class));
    }


    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(post("/admin/users/delete/5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService, times(1)).deleteUser(5L);
    }


    @Test
    void testViewProperties() throws Exception {
        when(propertyService.getAllProperties()).thenReturn(Collections.singletonList(new Property()));

        mockMvc.perform(get("/admin/properties"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("properties"))
                .andExpect(view().name("admin-properties"));
    }


    @Test
    void testApproveProperty() throws Exception {
        mockMvc.perform(get("/admin/properties/approve/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/properties"));

        verify(propertyService, times(1)).updatePropertyStatus(1L, "APPROVED");
    }

    @Test
    void testRejectProperty() throws Exception {
        mockMvc.perform(get("/admin/properties/reject/2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/properties"));

        verify(propertyService, times(1)).updatePropertyStatus(2L, "REJECTED");
    }



    @Test
    void testDeleteProperty() throws Exception {
        mockMvc.perform(get("/admin/properties/delete/3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/properties"));

        verify(propertyService, times(1)).deleteProperty(3L);
    }
}
