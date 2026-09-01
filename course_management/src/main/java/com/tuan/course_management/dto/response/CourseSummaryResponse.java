package com.tuan.course_management.dto.response;

import com.tuan.course_management.enums.CourseStatus;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseSummaryResponse {

    private Long id;
    private String title;
    private BigDecimal price;
    private Integer durationHours;
    private CourseStatus status;
    private String teacherName;
    private Double avgRating;
    private Long totalStudents;
}