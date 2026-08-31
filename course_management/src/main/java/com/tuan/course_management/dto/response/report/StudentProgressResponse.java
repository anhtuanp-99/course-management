package com.tuan.course_management.dto.response.report;

import lombok.*;

import java.util.List;

/**
 * DTO phản hồi thông tin báo cáo tiến độ học tập của sinh viên.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentProgressResponse {

    private Long studentId;
    private String studentName;
    private List<CourseProgress> courses;

    /**
     * DTO chứa thông tin tiến độ của sinh viên trong từng khóa học cụ thể.
     */
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseProgress {

        private Long courseId;
        private String courseTitle;
        private int totalLessons;
        private int completedLessons;
        private double completionPercentage;
    }
}