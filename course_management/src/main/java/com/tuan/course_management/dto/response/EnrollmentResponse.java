package com.tuan.course_management.dto.response;

import com.tuan.course_management.enums.EnrollmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentResponse {

    private Long id;
    private UserSummaryResponse student;
    private CourseSummaryResponse course;
    private EnrollmentStatus status;
    private Double progressPercentage;
    private LocalDateTime enrolledAt;
    private LocalDateTime completionDate;
}