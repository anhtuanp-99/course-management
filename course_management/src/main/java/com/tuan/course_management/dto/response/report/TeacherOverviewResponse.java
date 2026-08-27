package com.tuan.course_management.dto.response.report;

import lombok.*;

import java.util.List;

/**
 * DTO cho báo cáo tổng quan khóa học của một giảng viên.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherOverviewResponse {

    private Long teacherId;
    private String teacherName;
    private int totalCourses;
    private long totalEnrollments;
    private List<CourseSummary> courses;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CourseSummary {
        private Long courseId;
        private String courseTitle;
        private int totalLessons;
        private long enrollmentCount;
    }
}