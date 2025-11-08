package com.example.real_estate_project.service;

import com.example.real_estate_project.model.Property;
import com.example.real_estate_project.repository.PropertyRepository;
import com.example.real_estate_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository; // ✅ For saving owner updates

    // Directory to save uploaded images
    private final Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");

    /**
     * Save new property with uploaded image
     */
    public void savePropertyWithImage(Property property, MultipartFile imageFile) {
        try {
            // Create uploads directory if it doesn’t exist
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            if (imageFile != null && !imageFile.isEmpty()) {
                // Generate a unique filename for image
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path filePath = uploadDir.resolve(fileName);
                imageFile.transferTo(filePath.toFile());

                // Set relative URL for displaying the image
                property.setImageUrl("/uploads/" + fileName);
            }

            // ✅ Default property status
            if (property.getStatus() == null || property.getStatus().isEmpty()) {
                property.setStatus("PENDING"); // can be "PENDING" until admin approves
            }

            // ✅ Save owner info first (if present)
            if (property.getOwner() != null) {
                userRepository.save(property.getOwner());
            }

            // ✅ Save property
            propertyRepository.save(property);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save property image", e);
        }
    }

    /**
     * Save property (used for updates)
     */
    public Property saveProperty(Property property) {
        if (property.getOwner() != null) {
            userRepository.save(property.getOwner());
        }
        return propertyRepository.save(property); // ✅ return saved entity
    }


    /**
     * Get all properties
     */
    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    /**
     * Get all PENDING properties
     */
    public List<Property> getPendingProperties() {
        return propertyRepository.findByStatus("PENDING");
    }

    /**
     * Get all APPROVED properties
     */
    public List<Property> getAllApprovedProperties() {
        return propertyRepository.findByStatus("APPROVED");
    }

    /**
     * Get property by ID
     */
    public Property getById(Long id) {
        return propertyRepository.findById(id).orElse(null);
    }

    /**
     * Update property status dynamically (used by Admin APIs)
     */
    public boolean updatePropertyStatus(Long id, String status) {
        Optional<Property> optionalProperty = propertyRepository.findById(id);
        if (optionalProperty.isPresent()) {
            Property property = optionalProperty.get();
            property.setStatus(status);
            propertyRepository.save(property);
            return true;
        }
        return false;
    }

    /**
     * Approve property manually
     */
    public void approveProperty(Long id) {
        updatePropertyStatus(id, "APPROVED");
    }

    /**
     * Reject property manually
     */
    public void rejectProperty(Long id) {
        updatePropertyStatus(id, "REJECTED");
    }

    /**
     * Delete property by ID
     */
    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }

    /**
     * Count total properties
     */
    public long countAll() {
        return propertyRepository.count();
    }

    /**
     * Search with filters
     */
    public List<Property> searchProperties(String location, Double price, String type) {
        return propertyRepository.findByFilters(location, price, type);
    }

    public Property getPropertyById(Long id) {
        return getById(id);
    }
}
