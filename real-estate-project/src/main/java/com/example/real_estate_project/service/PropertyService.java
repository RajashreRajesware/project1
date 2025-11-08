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
    private final UserRepository userRepository;


    private final Path uploadDir = Paths.get(System.getProperty("user.dir"), "uploads");


    public void savePropertyWithImage(Property property, MultipartFile imageFile) {
        try {

            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            if (imageFile != null && !imageFile.isEmpty()) {

                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path filePath = uploadDir.resolve(fileName);
                imageFile.transferTo(filePath.toFile());


                property.setImageUrl("/uploads/" + fileName);
            }

            if (property.getStatus() == null || property.getStatus().isEmpty()) {
                property.setStatus("PENDING");
            }


            if (property.getOwner() != null) {
                userRepository.save(property.getOwner());
            }


            propertyRepository.save(property);

        } catch (IOException e) {
            throw new RuntimeException("Failed to save property image", e);
        }
    }


    public Property saveProperty(Property property) {
        if (property.getOwner() != null) {
            userRepository.save(property.getOwner());
        }
        return propertyRepository.save(property);
    }



    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }


    public List<Property> getPendingProperties() {
        return propertyRepository.findByStatus("PENDING");
    }


    public List<Property> getAllApprovedProperties() {
        return propertyRepository.findByStatus("APPROVED");
    }


    public Property getById(Long id) {
        return propertyRepository.findById(id).orElse(null);
    }


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


    public void approveProperty(Long id) {
        updatePropertyStatus(id, "APPROVED");
    }


    public void rejectProperty(Long id) {
        updatePropertyStatus(id, "REJECTED");
    }


    public void deleteProperty(Long id) {
        propertyRepository.deleteById(id);
    }


    public long countAll() {
        return propertyRepository.count();
    }


    public List<Property> searchProperties(String location, Double price, String type) {
        return propertyRepository.findByFilters(location, price, type);
    }

    public Property getPropertyById(Long id) {
        return getById(id);
    }
}
