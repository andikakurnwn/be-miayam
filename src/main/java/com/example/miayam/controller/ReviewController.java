package com.example.miayam.controller;

import com.example.miayam.dto.ApiResponse;
import com.example.miayam.dto.StoreGroupDTO;
import com.example.miayam.model.Review;
import com.example.miayam.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Review>>> getAllReviews() {
        return ResponseEntity.ok(ApiResponse.success("Successfully fetched all reviews", reviewService.getAllReviews()));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<List<StoreGroupDTO>>> getStoreGroupSummary() {
        return ResponseEntity.ok(ApiResponse.success("Successfully fetched store summary", reviewService.getStoreGroupSummary()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Review>> storeReview(@Valid @RequestBody com.example.miayam.dto.ReviewRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Successfully added review", reviewService.storeReview(request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.ok(ApiResponse.success("Successfully deleted review", null));
    }
}
