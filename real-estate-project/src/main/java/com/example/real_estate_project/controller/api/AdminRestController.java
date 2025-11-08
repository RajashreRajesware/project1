package com.example.real_estate_project.controller.api;

import com.example.real_estate_project.model.Property;
import com.example.real_estate_project.service.PropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final PropertyService propertyService;

    public AdminRestController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    // ✅ View all pending properties
    @GetMapping("/properties/pending")
    public List<Property> getPendingProperties() {
        return propertyService.getPendingProperties();
    }

    // ✅ Approve a property
    @PutMapping("/properties/{id}/approve")
    public ResponseEntity<String> approveProperty(@PathVariable Long id) {
        boolean approved = propertyService.updatePropertyStatus(id, "APPROVED");
        return approved
                ? ResponseEntity.ok("Property approved successfully")
                : ResponseEntity.badRequest().body("Property not found");
    }

    // ✅ Reject a property
    @PutMapping("/properties/{id}/reject")
    public ResponseEntity<String> rejectProperty(@PathVariable Long id) {
        boolean rejected = propertyService.updatePropertyStatus(id, "REJECTED");
        return rejected
                ? ResponseEntity.ok("Property rejected successfully")
                : ResponseEntity.badRequest().body("Property not found");
    }

    // ✅ View all approved properties (optional)
    @GetMapping("/properties/approved")
    public List<Property> getApprovedProperties() {
        return propertyService.getAllApprovedProperties();
    }
}
