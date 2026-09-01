package com.tuan.course_management.dto.response;

import com.tuan.course_management.enums.CourseStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDetailResponse {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Integer durationHours;
    private CourseStatus status;
    private UserSummaryResponse teacher;
    private Double avgRating;
    private Long totalStudents;
    private Long totalLessons;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}