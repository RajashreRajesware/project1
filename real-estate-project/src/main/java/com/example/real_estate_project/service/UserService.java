package com.example.real_estate_project.service;

import com.example.real_estate_project.model.User;
import com.example.real_estate_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public void registerUser(User user) {

        user.setEmail(user.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(user.getPassword()));


        if (user.getRole() != null) {
            if (!user.getRole().startsWith("ROLE_")) {
                user.setRole("ROLE_" + user.getRole().toUpperCase());
            }
        } else {

            user.setRole("ROLE_CUSTOMER");
        }

        userRepository.save(user);
    }



    public void updateUser(User updatedUser) {
        User existingUser = userRepository.findById(updatedUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + updatedUser.getId()));

        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setEmail(updatedUser.getEmail());

        if (updatedUser.getRole() != null) {
            if (!updatedUser.getRole().startsWith("ROLE_")) {
                existingUser.setRole("ROLE_" + updatedUser.getRole().toUpperCase());
            } else {
                existingUser.setRole(updatedUser.getRole());
            }
        }


        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        userRepository.save(existingUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }


    public List<User> getAllUsers() {
        return userRepository.findAll();
    }


    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public User getById(Long id) {
        return userRepository.findById(id).orElse(null);
    }


    public long countAll() {
        return userRepository.count();
    }
}
