package com.tuan.course_management.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProgressReportResponse {
    private Long studentId;
    private String studentName;
    private String email;
    private long totalEnrolledCourses;
    private long completedCourses;
    private double overallCompletionRate; // Phần trăm hoàn thành (ví dụ: 60.5%)
}