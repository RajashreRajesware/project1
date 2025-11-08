package com.example.real_estate_project.controller;


import com.example.real_estate_project.model.Property;
import com.example.real_estate_project.model.User;
import com.example.real_estate_project.repository.UserRepository;
import com.example.real_estate_project.service.ImageUploadService;
import com.example.real_estate_project.service.PropertyService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/properties")
public class PropertyController {
    private final PropertyService propertyService;
    private final ImageUploadService imageUploadService;
    private final UserRepository userRepository;

    public PropertyController(PropertyService propertyService, ImageUploadService imageUploadService, UserRepository userRepository) {
        this.propertyService = propertyService;
        this.imageUploadService = imageUploadService;
        this.userRepository = userRepository;
    }

    @GetMapping("/list")
    public String listProperties(
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) String type,
            Model model) {


        location = (location != null && !location.trim().isEmpty()) ? location.trim() : null;
        type = (type != null && !type.trim().isEmpty()) ? type.trim() : null;

        List<Property> properties;




        if (location != null || price != null || type != null) {
            properties = propertyService.searchProperties(location, price, type);
        } else {
            properties = propertyService.getAllProperties();
        }

        model.addAttribute("properties", properties);
        return "property-list";
    }



    @GetMapping("/pending")
    public String viewPending(Model model) {
        model.addAttribute("properties", propertyService.getPendingProperties());
        return "property-list";
    }

    @GetMapping("/form")
    public String propertyForm(Model model) {
        Property property = new Property();
        property.setPrice(null);
        model.addAttribute("property", property);
        return "property-form";
    }

    @GetMapping("/search")
    public String showSearchPage() {
        return "search";
    }
    @GetMapping("/add")
    public String showAddPropertyForm(Model model) {
        Property property = new Property();
        property.setOwner(new User());
        model.addAttribute("property", property);
        return "add-property";
    }

    @PostMapping("/save")
    public String saveProperty(@ModelAttribute Property property,
                               @RequestParam("imageFile") MultipartFile imageFile,
                               Authentication authentication) throws IOException {
        String email = authentication.getName();

        User currentUser = userRepository.findByEmail(email).orElse(null);
        if (currentUser != null) {
            property.setOwner(currentUser);
        }

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = imageUploadService.uploadImage(imageFile);
            property.setImageUrl(imageUrl);
        }

        if (property.getStatus() == null || property.getStatus().isEmpty()) {
            property.setStatus("PENDING");
        }

        propertyService.saveProperty(property);
        return "redirect:/properties/list";
    }

    @GetMapping("/view/{id}")
    public String viewProperty(@PathVariable Long id, Model model) {
        Property property = propertyService.getById(id);
        model.addAttribute("property", property);
        return "view-property";
    }

    @GetMapping("/edit/{id}")
    public String editProperty(@PathVariable Long id, Model model) {
        Property property = propertyService.getById(id);
        if (property == null) {
            return "redirect:/properties/list";
        }

        if (property.getOwner() == null) {
            property.setOwner(new User());
        }

        model.addAttribute("property", property);
        return "property-form";
    }


    @PostMapping("/update")
    public String updateProperty(@ModelAttribute Property property,
                                 @RequestParam("imageFile") MultipartFile imageFile) throws IOException {

        Property existing = propertyService.getById(property.getId());


        if (existing.getOwner() == null) {
            existing.setOwner(new User());
        }

        existing.getOwner().setFullName(property.getOwner().getFullName());
        existing.getOwner().setEmail(property.getOwner().getEmail());
        userRepository.save(existing.getOwner());


        existing.setTitle(property.getTitle());
        existing.setDescription(property.getDescription());
        existing.setPrice(property.getPrice());
        existing.setType(property.getType());
        existing.setLocation(property.getLocation());


        if (imageFile != null && !imageFile.isEmpty()) {
            String imageUrl = imageUploadService.uploadImage(imageFile);
            existing.setImageUrl(imageUrl);
        }

        propertyService.saveProperty(existing);
        return "redirect:/properties/list";
    }

}
