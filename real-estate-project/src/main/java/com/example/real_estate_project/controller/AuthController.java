package com.example.real_estate_project.controller;

import com.example.real_estate_project.model.User;
import com.example.real_estate_project.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/dashboard")
    public String userDashboard(Model model, Authentication authentication) {
        model.addAttribute("userEmail", authentication.getName());
        return "dashboard";
    }

    @GetMapping({"/", "/register"})
    public String registerForm(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            for (var auth : authentication.getAuthorities()) {
                String role = auth.getAuthority();
                if (role.equals("ROLE_ADMIN")) {
                    return "redirect:/admin/dashboard";
                } else if (role.equals("ROLE_CUSTOMER")) {
                    return "redirect:/dashboard";
                }
            }
        }
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user) {

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_CUSTOMER");
        } else if (!user.getRole().startsWith("ROLE_")) {
            user.setRole("ROLE_" + user.getRole().toUpperCase());
        }

        userService.registerUser(user);

        return "redirect:/login";
    }


    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            for (var auth : authentication.getAuthorities()) {
                String role = auth.getAuthority();
                if (role.equals("ROLE_ADMIN")) {
                    return "redirect:/admin/dashboard";
                } else if (role.equals("ROLE_CUSTOMER")) {
                    return "redirect:/dashboard";
                }
            }
        }
        return "login";
    }
}
