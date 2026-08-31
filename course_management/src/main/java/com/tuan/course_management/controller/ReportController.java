package com.tuan.course_management.controller;

import com.tuan.course_management.dto.response.ApiResponse;
import com.tuan.course_management.dto.response.report.StudentProgressResponse;
import com.tuan.course_management.dto.response.report.TeacherOverviewResponse;
import com.tuan.course_management.dto.response.report.TopCourseResponse;
import com.tuan.course_management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller tiếp nhận và xử lý các Endpoint RESTful báo cáo thống kê (Chỉ ADMIN).
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Báo cáo top khóa học phổ biến nhất (Chỉ ADMIN).
     * Đáp ứng STT 37.
     */
    @GetMapping("/top_courses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TopCourseResponse>>> getTopCourses(
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        List<TopCourseResponse> response = reportService.getTopCourses(limit);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Báo cáo tiến độ học của sinh viên (Chỉ ADMIN).
     * Đáp ứng STT 38.
     */
    @GetMapping("/student_progress/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<StudentProgressResponse>> getStudentProgress(
            @PathVariable("studentId") Long studentId) {
        StudentProgressResponse response = reportService.getStudentProgress(studentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Báo cáo tổng quan khóa học của giảng viên (Chỉ ADMIN).
     * Đáp ứng STT 39.
     */
    @GetMapping("/teacher_courses_overview/{teacherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TeacherOverviewResponse>> getTeacherOverview(
            @PathVariable("teacherId") Long teacherId) {
        TeacherOverviewResponse response = reportService.getTeacherOverview(teacherId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}