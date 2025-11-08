package com.example.real_estate_project.service;

import com.example.real_estate_project.model.User;
import com.example.real_estate_project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --------------------------------------------------
    // ✅ TEST: loadUserByUsername() — valid email
    // --------------------------------------------------
    @Test
    void testLoadUserByUsername_Success() {
        User user = new User();
        user.setEmail("test@mail.com");
        user.setPassword("encodedPass");
        user.setRole("ROLE_USER");

        when(userRepository.findByEmail("test@mail.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = userDetailsService.loadUserByUsername("  TEST@MAIL.COM  ");

        verify(userRepository, times(1)).findByEmail("test@mail.com");

        assertNotNull(userDetails);
        assertEquals("test@mail.com", userDetails.getUsername());
        assertEquals("encodedPass", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    // --------------------------------------------------
    // ✅ TEST: loadUserByUsername() — user not found
    // --------------------------------------------------
    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(userRepository.findByEmail("missing@mail.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("missing@mail.com"));

        assertEquals("User not found with email: missing@mail.com", exception.getMessage());
    }
}
