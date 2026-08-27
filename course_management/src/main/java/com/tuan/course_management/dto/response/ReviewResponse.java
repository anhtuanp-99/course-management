package com.tuan.course_management.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO trả về thông tin đánh giá.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {

    private Long id;
    private int rating;
    private String comment;
    private Long studentId;
    private String studentName;
    private Long courseId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}