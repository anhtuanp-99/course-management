package com.tuan.course_management.dto.response.report;

import lombok.*;

import java.util.List;

/**
 * DTO cho báo cáo tiến độ học tập của một sinh viên.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentProgessResponse {

    private Long studentId;
    private String studentName;
    private List<CourseProgress> courses;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CourseProgress{
        private Long courseId;
        private String courseName;
        private int totalLessons;
        private int completedLessons;
        private double completionPercentage;
    }
}
