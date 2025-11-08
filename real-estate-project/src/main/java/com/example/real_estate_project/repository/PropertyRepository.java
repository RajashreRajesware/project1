package com.example.real_estate_project.repository;

import com.example.real_estate_project.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {


    @Query("SELECT p FROM Property p WHERE " +
            "(:location IS NULL OR LOWER(p.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
            "(:price IS NULL OR p.price <= :price) AND " +
            "(:type IS NULL OR p.type = :type)")
    List<Property> findByFilters(@Param("location") String location,
                                 @Param("price") Double price,
                                 @Param("type") String type);

    List<Property> findByStatus(String status);

    List<Property> findByLocationContainingIgnoreCase(String keyword);

    List<Property> findByTypeIgnoreCase(String type);

    List<Property> findByPriceBetween(double min, double max);

}
