package com.example.miayam.service;

import com.example.miayam.dto.StoreGroupDTO;
import com.example.miayam.model.Review;
import com.example.miayam.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    
    private final ReviewRepository reviewRepository;

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<StoreGroupDTO> getStoreGroupSummary() {
        return reviewRepository.getStoreGroupSummary();
    }

    public Review storeReview(com.example.miayam.dto.ReviewRequestDTO dto) {
        Review review = Review.builder()
                .reviewerName(dto.getReviewerName())
                .storeName(dto.getStoreName())
                .location(dto.getLocation())
                .rating(dto.getRating())
                .review(dto.getReview())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();
        return reviewRepository.save(review);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}
