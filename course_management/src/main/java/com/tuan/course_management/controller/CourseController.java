package com.tuan.course_management.controller;

import com.tuan.course_management.dto.request.CourseCreateRequest;
import com.tuan.course_management.dto.request.CourseUpdateRequest;
import com.tuan.course_management.dto.response.ApiResponse;
import com.tuan.course_management.dto.response.CourseDetailResponse;
import com.tuan.course_management.dto.response.CourseSummaryResponse;
import com.tuan.course_management.dto.response.PageResponse;
import com.tuan.course_management.enums.CourseStatus;
import com.tuan.course_management.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controller tiếp nhận và xử lý các Endpoint RESTful liên quan đến khóa học.
 */
@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * Lấy danh sách khóa học có phân trang, hỗ trợ tìm kiếm và lọc động.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<CourseSummaryResponse>>> getCourses(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "teacherId", required = false) Long teacherId,
            @RequestParam(name = "status", required = false) CourseStatus status,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir) {

        PageResponse<CourseSummaryResponse> response = courseService.getCourses(
                page, size, sortBy, sortDir, search, teacherId, status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Lấy chi tiết thông tin khóa học (Cho phép STUDENT, TEACHER và ADMIN đã đăng nhập).
     */
    @GetMapping("/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CourseDetailResponse>> getCourse(
            @PathVariable("courseId") Long courseId) {
        CourseDetailResponse response = courseService.getCourseById(courseId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Tạo mới một khóa học (Chỉ ADMIN).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseSummaryResponse>> createCourse(
            @Valid @RequestBody CourseCreateRequest request) {
        CourseSummaryResponse response = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Tạo khóa học thành công", response));
    }

    /**
     * Cập nhật thông tin khóa học (Chỉ ADMIN).
     */
    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseSummaryResponse>> updateCourse(
            @PathVariable("courseId") Long courseId,
            @Valid @RequestBody CourseUpdateRequest request) {
        CourseSummaryResponse response = courseService.updateCourse(courseId, request);
        return ResponseEntity.ok(ApiResponse.success(200, "Cập nhật khóa học thành công", response));
    }

    /**
     * Cập nhật trạng thái hiển thị của khóa học (Chỉ ADMIN).
     */
    @PutMapping("/{courseId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseSummaryResponse>> updateCourseStatus(
            @PathVariable("courseId") Long courseId,
            @RequestParam("status") CourseStatus status) {
        CourseSummaryResponse response = courseService.updateCourseStatus(courseId, status);
        return ResponseEntity.ok(ApiResponse.success(200, "Cập nhật trạng thái thành công", response));
    }

    /**
     * Xóa khóa học khỏi hệ thống (Chỉ ADMIN).
     */
    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(
            @PathVariable("courseId") Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(200, "Xóa khóa học thành công", null));
    }
}