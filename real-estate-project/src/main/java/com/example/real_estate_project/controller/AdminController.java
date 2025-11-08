package com.example.real_estate_project.controller;

import com.example.real_estate_project.model.Property;
import com.example.real_estate_project.model.User;
import com.example.real_estate_project.service.PropertyService;
import com.example.real_estate_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PropertyService propertyService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String showDashboard(Model model, Authentication authentication) {
        List<Property> allProperties = propertyService.getAllProperties();
        List<User> allUsers = userService.getAllUsers();

        model.addAttribute("userEmail", authentication != null ? authentication.getName() : "Admin");
        model.addAttribute("propertyCount", allProperties.size());
        model.addAttribute("userCount", allUsers.size());
        return "admin-dashboard";
    }


    @GetMapping("/users")
    public String viewUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin-users";
    }

    @GetMapping("/users/edit/{id}")
    public String editUserForm(@PathVariable Long id, Model model) {
        User user = userService.getById(id);
        model.addAttribute("user", user);
        return "edit-user";
    }


    @PostMapping("/users/update")
    public String updateUser(@ModelAttribute("user") User user) {
        userService.updateUser(user);
        return "redirect:/admin/users";
    }


    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/users";
    }



    @GetMapping("/properties")
    public String viewProperties(Model model) {
        model.addAttribute("properties", propertyService.getAllProperties());
        return "admin-properties";
    }

    @GetMapping("/properties/approve/{id}")
    public String approveProperty(@PathVariable Long id) {
        propertyService.updatePropertyStatus(id, "APPROVED");
        return "redirect:/admin/properties";
    }

    @GetMapping("/properties/reject/{id}")
    public String rejectProperty(@PathVariable Long id) {
        propertyService.updatePropertyStatus(id, "REJECTED");
        return "redirect:/admin/properties";
    }

    @GetMapping("/properties/delete/{id}")
    public String deleteProperty(@PathVariable Long id) {
        propertyService.deleteProperty(id);
        return "redirect:/admin/properties";
    }
}
