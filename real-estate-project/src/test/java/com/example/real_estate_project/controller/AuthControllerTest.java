package com.example.real_estate_project.controller;

import com.example.real_estate_project.model.User;
import com.example.real_estate_project.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collection;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testUserDashboard() throws Exception {
        when(authentication.getName()).thenReturn("test@example.com");

        mockMvc.perform(get("/dashboard").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(model().attribute("userEmail", "test@example.com"))
                .andExpect(view().name("dashboard"));
    }


    @Test
    void testRegisterForm_NewUser() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("register"));
    }


    @Test
    void testRegisterForm_AdminRedirect() throws Exception {
        GrantedAuthority adminAuthority = () -> "ROLE_ADMIN";
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(adminAuthority);

        when(authentication.isAuthenticated()).thenReturn(true);
        doReturn(authorities).when(authentication).getAuthorities();

        mockMvc.perform(get("/register").principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));
    }


    @Test
    void testRegisterForm_CustomerRedirect() throws Exception {
        GrantedAuthority customerAuthority = () -> "ROLE_CUSTOMER";
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(customerAuthority);

        when(authentication.isAuthenticated()).thenReturn(true);
        doReturn(authorities).when(authentication).getAuthorities();

        mockMvc.perform(get("/register").principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }


    @Test
    void testRegisterUser_Post_Success() throws Exception {
        mockMvc.perform(post("/register")
                        .param("fullName", "John Doe")
                        .param("email", "john@example.com")
                        .param("password", "12345")
                        .param("role", "CUSTOMER"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        verify(userService, times(1)).registerUser(any(User.class));
    }


    @Test
    void testLoginPage_Unauthenticated() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }


    @Test
    void testLoginPage_AdminRedirect() throws Exception {
        GrantedAuthority adminAuthority = () -> "ROLE_ADMIN";
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(adminAuthority);

        when(authentication.isAuthenticated()).thenReturn(true);
        doReturn(authorities).when(authentication).getAuthorities();

        mockMvc.perform(get("/login").principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/dashboard"));
    }


    @Test
    void testLoginPage_CustomerRedirect() throws Exception {
        GrantedAuthority customerAuthority = () -> "ROLE_CUSTOMER";
        Collection<? extends GrantedAuthority> authorities = Collections.singletonList(customerAuthority);

        when(authentication.isAuthenticated()).thenReturn(true);
        doReturn(authorities).when(authentication).getAuthorities();

        mockMvc.perform(get("/login").principal(authentication))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/dashboard"));
    }
}
