package com.example.real_estate_project.service;

import com.example.real_estate_project.model.User;
import com.example.real_estate_project.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --------------------------------------------------
    // ✅ TEST: registerUser()
    // --------------------------------------------------
    @Test
    void testRegisterUser_WithRoleProvided() {
        User user = new User();
        user.setEmail("Test@Email.com");
        user.setPassword("12345");
        user.setRole("admin");

        when(passwordEncoder.encode("12345")).thenReturn("encodedPassword");

        userService.registerUser(user);

        verify(userRepository, times(1)).save(user);
        assertEquals("test@email.com", user.getEmail());
        assertEquals("encodedPassword", user.getPassword());
        assertEquals("ROLE_ADMIN", user.getRole());
    }

    @Test
    void testRegisterUser_NoRoleProvided() {
        User user = new User();
        user.setEmail("  sample@MAIL.COM  ");
        user.setPassword("pass123");

        when(passwordEncoder.encode("pass123")).thenReturn("encodedPass");

        userService.registerUser(user);

        verify(userRepository).save(user);
        assertEquals("sample@mail.com", user.getEmail());
        assertEquals("encodedPass", user.getPassword());
        assertEquals("ROLE_CUSTOMER", user.getRole());
    }

    // --------------------------------------------------
    // ✅ TEST: updateUser()
    // --------------------------------------------------
    @Test
    void testUpdateUser_SuccessWithPasswordChange() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setPassword("oldPass");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setFullName("New Name");
        updatedUser.setEmail("new@email.com");
        updatedUser.setPassword("newPass");
        updatedUser.setRole("manager");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newPass")).thenReturn("encodedNewPass");

        userService.updateUser(updatedUser);

        verify(userRepository).save(existingUser);
        assertEquals("New Name", existingUser.getFullName());
        assertEquals("new@email.com", existingUser.getEmail());
        assertEquals("encodedNewPass", existingUser.getPassword());
        assertEquals("ROLE_MANAGER", existingUser.getRole());
    }

    @Test
    void testUpdateUser_WithoutPasswordChange() {
        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setPassword("oldPass");

        User updatedUser = new User();
        updatedUser.setId(2L);
        updatedUser.setFullName("Updated User");
        updatedUser.setEmail("updated@mail.com");
        updatedUser.setPassword(""); // no change
        updatedUser.setRole("ROLE_ADMIN");

        when(userRepository.findById(2L)).thenReturn(Optional.of(existingUser));

        userService.updateUser(updatedUser);

        verify(userRepository).save(existingUser);
        assertEquals("Updated User", existingUser.getFullName());
        assertEquals("updated@mail.com", existingUser.getEmail());
        assertEquals("oldPass", existingUser.getPassword()); // unchanged
        assertEquals("ROLE_ADMIN", existingUser.getRole());
    }

    @Test
    void testUpdateUser_UserNotFound() {
        User updatedUser = new User();
        updatedUser.setId(100L);

        when(userRepository.findById(100L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> userService.updateUser(updatedUser));

        assertEquals("User not found with ID: 100", exception.getMessage());
    }

    // --------------------------------------------------
    // ✅ TEST: deleteUser()
    // --------------------------------------------------
    @Test
    void testDeleteUser() {
        userService.deleteUser(5L);
        verify(userRepository, times(1)).deleteById(5L);
    }

    // --------------------------------------------------
    // ✅ TEST: getAllUsers()
    // --------------------------------------------------
    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(new User(), new User()));
        List<User> users = userService.getAllUsers();
        assertEquals(2, users.size());
    }

    // --------------------------------------------------
    // ✅ TEST: findByEmail()
    // --------------------------------------------------
    @Test
    void testFindByEmail() {
        User user = new User();
        user.setEmail("abc@mail.com");
        when(userRepository.findByEmail("abc@mail.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("abc@mail.com");
        assertTrue(result.isPresent());
        assertEquals("abc@mail.com", result.get().getEmail());
    }

    // --------------------------------------------------
    // ✅ TEST: getById()
    // --------------------------------------------------
    @Test
    void testGetById_Found() {
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getById(1L);
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetById_NotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertNull(userService.getById(2L));
    }

    // --------------------------------------------------
    // ✅ TEST: countAll()
    // --------------------------------------------------
    @Test
    void testCountAll() {
        when(userRepository.count()).thenReturn(7L);
        long count = userService.countAll();
        assertEquals(7L, count);
    }
}
