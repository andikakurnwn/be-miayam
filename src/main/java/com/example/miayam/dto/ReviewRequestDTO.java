package com.example.miayam.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReviewRequestDTO {

    @NotBlank(message = "Reviewer name is required")
    private String reviewerName;

    @NotBlank(message = "Store name is required")
    private String storeName;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Rating is required")
    @Min(value = 0, message = "Rating must be between 0 and 5")
    @Max(value = 5, message = "Rating must be between 0 and 5")
    private Double rating;

    @NotBlank(message = "Review content is required")
    private String review;

    private Double latitude;
    private Double longitude;
}
