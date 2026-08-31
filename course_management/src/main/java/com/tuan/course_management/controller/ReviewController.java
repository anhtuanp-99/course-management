package com.tuan.course_management.controller;

import com.tuan.course_management.dto.request.ReviewRequest;
import com.tuan.course_management.dto.response.ApiResponse;
import com.tuan.course_management.dto.response.PageResponse;
import com.tuan.course_management.dto.response.ReviewResponse;
import com.tuan.course_management.security.UserPrincipal;
import com.tuan.course_management.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller tiếp nhận và xử lý các Endpoint RESTful liên quan đến đánh giá khóa học.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Lấy danh sách đánh giá của khóa học (Yêu cầu đã đăng nhập).
     * Đáp ứng STT 40.
     */
    @GetMapping("/courses/{courseId}/reviews")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getReviewsByCourse(
            @PathVariable("courseId") Long courseId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir) {

        PageResponse<ReviewResponse> response = reviewService.getReviewsByCourse(courseId, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Gửi đánh giá khóa học (Chỉ STUDENT đã đăng ký).
     * Trả về HTTP Status 201 CREATED theo chuẩn RESTful API.
     * Đáp ứng STT 41.
     */
    @PostMapping("/courses/{courseId}/reviews")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable("courseId") Long courseId,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        ReviewResponse response = reviewService.createReview(courseId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Gửi đánh giá thành công", response));
    }

    /**
     * Cập nhật đánh giá (Chỉ chính chủ học viên hoặc ADMIN).
     * Đáp ứng STT 42.
     */
    @PutMapping("/reviews/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable("reviewId") Long reviewId,
            @Valid @RequestBody ReviewRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        ReviewResponse response = reviewService.updateReview(reviewId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật đánh giá thành công", response));
    }

    /**
     * Xóa đánh giá khỏi hệ thống (Chỉ chính chủ học viên hoặc ADMIN).
     * Đáp ứng STT 43.
     */
    @DeleteMapping("/reviews/{reviewId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        reviewService.deleteReview(reviewId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Xóa đánh giá thành công", null));
    }
}