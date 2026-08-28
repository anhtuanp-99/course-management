package com.tuan.course_management.mapper;

import com.tuan.course_management.dto.response.ReviewResponse;
import com.tuan.course_management.entity.Review;

/**
 * Mapper chuyển đổi giữa entity Review và ReviewResponse DTO.
 */
public class ReviewMapper {

    /**
     * Chuyển Review entity sang ReviewResponse.
     */
    public static ReviewResponse toResponse(Review review) {
        if (review == null) return null;

        return ReviewResponse.builder()
                .id(review.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .studentId(review.getStudent() != null ? review.getStudent().getId() : null)
                .studentName(review.getStudent() != null ? review.getStudent().getFullName() : null)
                .courseId(review.getCourse() != null ? review.getCourse().getId() : null)
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}