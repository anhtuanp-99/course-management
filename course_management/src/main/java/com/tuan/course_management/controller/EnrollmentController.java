package com.tuan.course_management.controller;

import com.tuan.course_management.dto.request.EnrollmentCreateRequest;
import com.tuan.course_management.dto.response.ApiResponse;
import com.tuan.course_management.dto.response.EnrollmentResponse;
import com.tuan.course_management.dto.response.LessonProgressResponse;
import com.tuan.course_management.dto.response.PageResponse;
import com.tuan.course_management.security.UserPrincipal;
import com.tuan.course_management.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller tiếp nhận và xử lý các Endpoint RESTful liên quan đến đăng ký và tiến độ học tập.
 */
@RestController
@RequestMapping("/api/v1/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    /**
     * Lấy danh sách khóa học cá nhân đã đăng ký (Yêu cầu đã đăng nhập).
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<EnrollmentResponse>>> getEnrollments(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "enrolledAt") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir) {

        PageResponse<EnrollmentResponse> response = enrollmentService.getEnrollments(
                currentUser, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Đăng ký tham gia khóa học mới (Yêu cầu đã đăng nhập).
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enroll(
            @Valid @RequestBody EnrollmentCreateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        EnrollmentResponse response = enrollmentService.enroll(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Đăng ký khóa học thành công", response));
    }

    /**
     * Xem chi tiết tiến độ các bài học thuộc khóa học đã đăng ký (Yêu cầu chính chủ hoặc ADMIN).
     */
    @GetMapping("/{enrollmentId}/progress")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<LessonProgressResponse>>> getEnrollmentProgress(
            @PathVariable("enrollmentId") Long enrollmentId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        List<LessonProgressResponse> response = enrollmentService.getEnrollmentProgress(enrollmentId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Đánh dấu hoàn thành một bài học (Yêu cầu chính chủ học viên).
     */
    @PutMapping("/{enrollmentId}/lessons/{lessonId}/complete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<LessonProgressResponse>> completeLesson(
            @PathVariable("enrollmentId") Long enrollmentId,
            @PathVariable("lessonId") Long lessonId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        LessonProgressResponse response = enrollmentService.completeLesson(enrollmentId, lessonId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(200, "Đánh dấu hoàn thành bài học thành công", response));
    }
}