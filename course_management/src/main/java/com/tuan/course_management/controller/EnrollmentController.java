package com.tuan.course_management.controller;

import com.tuan.course_management.dto.request.EnrollmentRequest;
import com.tuan.course_management.dto.response.ApiResponse;
import com.tuan.course_management.dto.response.EnrollmentDetailResponse;
import com.tuan.course_management.dto.response.EnrollmentResponse;
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

/**
 * Controller tiếp nhận và xử lý các Endpoint RESTful liên quan đến đăng ký và tiến độ học tập.
 */
@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    /**
     * Lấy danh sách khóa học cá nhân đã đăng ký (Yêu cầu đã đăng nhập).
     * Đáp ứng STT 22.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<EnrollmentResponse>>> getEnrollments(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "enrolledAt") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir) {

        PageResponse<EnrollmentResponse> response = enrollmentService.getEnrollments(
                currentUser, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Đăng ký tham gia khóa học mới (Yêu cầu đã đăng nhập).
     * Trả về HTTP Status 201 CREATED theo chuẩn RESTful API.
     * Đáp ứng STT 23.
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enroll(
            @Valid @RequestBody EnrollmentRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        EnrollmentResponse response = enrollmentService.enroll(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng ký khóa học thành công", response));
    }

    /**
     * Xem chi tiết lộ trình và tiến độ học tập của khóa học (Yêu cầu chính chủ hoặc ADMIN).
     * Đáp ứng STT 24.
     */
    @GetMapping("/{enrollmentId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<EnrollmentDetailResponse>> getEnrollmentDetail(
            @PathVariable("enrollmentId") Long enrollmentId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        EnrollmentDetailResponse response = enrollmentService.getEnrollmentDetail(enrollmentId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Đánh dấu hoàn thành một bài học (Yêu cầu chính chủ học viên).
     * Đáp ứng STT 25.
     */
    @PutMapping("/{enrollmentId}/lessons/{lessonId}/complete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> completeLesson(
            @PathVariable("enrollmentId") Long enrollmentId,
            @PathVariable("lessonId") Long lessonId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        enrollmentService.completeLesson(enrollmentId, lessonId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Đánh dấu hoàn thành bài học thành công", null));
    }
}