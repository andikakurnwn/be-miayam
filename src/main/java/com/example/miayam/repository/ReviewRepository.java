package com.example.miayam.repository;

import com.example.miayam.dto.StoreGroupDTO;
import com.example.miayam.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query(value = "SELECT " +
            "store_name AS \"storeName\", " +
            "location AS \"location\", " +
            "CAST(ROUND(CAST(AVG(rating) AS numeric), 1) AS DOUBLE PRECISION) AS \"avgRating\", " +
            "CAST(COUNT(*) AS INTEGER) AS \"reviewCount\", " +
            "MAX(latitude) AS \"latitude\", " +
            "MAX(longitude) AS \"longitude\" " +
            "FROM reviews " +
            "GROUP BY store_name, location", nativeQuery = true)
    List<StoreGroupDTO> getStoreGroupSummary();
}
