package com.tuan.course_management.controller;

import com.tuan.course_management.dto.request.CourseRequest;
import com.tuan.course_management.dto.request.UpdateCourseStatusRequest;
import com.tuan.course_management.dto.response.ApiResponse;
import com.tuan.course_management.dto.response.CourseDetailResponse;
import com.tuan.course_management.dto.response.CourseResponse;
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
 * Controller quản lý các Endpoint RESTful liên quan đến khóa học.
 */
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    /**
     * Lấy danh sách khóa học có phân trang và bộ lọc (yêu cầu đã đăng nhập).
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<CourseResponse>>> getCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) CourseStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        PageResponse<CourseResponse> response = courseService.getCourses(
                page, size, sortBy, sortDir, search, teacherId, status);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Lấy chi tiết thông tin khóa học kèm danh sách bài học (yêu cầu đã đăng nhập).
     */
    @GetMapping("/{courseId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CourseDetailResponse>> getCourse(@PathVariable Long courseId) {
        CourseDetailResponse response = courseService.getCourseById(courseId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Tạo mới một khóa học (Chỉ ADMIN).
     * Trả về HTTP Status 201 CREATED theo đúng chuẩn RESTful API.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CourseRequest request) {
        CourseResponse response = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo khóa học thành công", response));
    }

    /**
     * Cập nhật toàn bộ thông tin khóa học (Chỉ ADMIN).
     */
    @PutMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseRequest request) {
        CourseResponse response = courseService.updateCourse(courseId, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật khóa học thành công", response));
    }

    /**
     * Cập nhật một phần: Trạng thái của khóa học (Chỉ ADMIN).
     * Sử dụng @PatchMapping đúng chuẩn RESTful cho thao tác partial update.
     */
    @PatchMapping("/{courseId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourseStatus(
            @PathVariable Long courseId,
            @Valid @RequestBody UpdateCourseStatusRequest request) {
        CourseResponse response = courseService.updateCourseStatus(courseId, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái thành công", response));
    }

    /**
     * Xóa khóa học khỏi hệ thống (Chỉ ADMIN).
     */
    @DeleteMapping("/{courseId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success("Xóa khóa học thành công", null));
    }
}