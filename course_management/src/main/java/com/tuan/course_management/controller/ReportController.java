package com.tuan.course_management.controller;

import com.tuan.course_management.dto.response.*;
import com.tuan.course_management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Báo cáo top khóa học phổ biến nhất có phân trang (Chỉ ADMIN).
     */
    @GetMapping("/top-courses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<CourseSummaryResponse>>> getTopCourses(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        PageResponse<CourseSummaryResponse> response = reportService.getTopCourses(page, size);
        return ResponseEntity.ok(ApiResponse.success(response));
    }


    /**
     * Lấy báo cáo thống kê tiến độ học tập chi tiết của học viên.
     *
     * @param studentId ID của học viên cần xuất báo cáo
     * @return ApiResponse chứa dữ liệu tiến độ (tổng khóa học, hoàn thành, tỷ lệ %)
     */
    @GetMapping("/student-progress/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER') or authentication.principal.id == #studentId")
    public ResponseEntity<ApiResponse<StudentProgressReportResponse>> getStudentProgress(
            @PathVariable Long studentId) {

        StudentProgressReportResponse data = reportService.getStudentProgressReport(studentId);
        return ResponseEntity.ok(ApiResponse.success(200, "Lấy báo cáo tiến độ học tập thành công", data));
    }

    /**
     * Báo cáo tổng quan khóa học của giảng viên (Chỉ ADMIN).
     */
    @GetMapping("/teacher-overview/{teacherId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TeacherReportResponse>> getTeacherOverview(
            @PathVariable("teacherId") Long teacherId) {

        TeacherReportResponse response = reportService.getTeacherOverview(teacherId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}