package com.tuan.course_management.controller;

import com.tuan.course_management.dto.request.LessonCreateRequest;
import com.tuan.course_management.dto.request.LessonUpdateRequest;
import com.tuan.course_management.dto.response.ApiResponse;
import com.tuan.course_management.dto.response.LessonResponse;
import com.tuan.course_management.dto.response.LessonSummaryResponse;
import com.tuan.course_management.dto.response.PageResponse;
import com.tuan.course_management.security.UserPrincipal;
import com.tuan.course_management.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller tiếp nhận và xử lý các Endpoint RESTful liên quan đến bài học.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;

    /**
     * Lấy danh sách bài học đã xuất bản của một khóa học.
     */
    @GetMapping("/courses/{courseId}/lessons")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<LessonSummaryResponse>>> getPublishedLessons(
            @PathVariable("courseId") Long courseId,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "orderIndex") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "asc") String sortDir) {

        PageResponse<LessonSummaryResponse> response = lessonService.getPublishedLessons(
                courseId, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Lấy chi tiết thông tin một bài học.
     */
    @GetMapping("/lessons/{lessonId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<LessonResponse>> getLesson(
            @PathVariable("lessonId") Long lessonId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        LessonResponse response = lessonService.getLessonById(lessonId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Thêm bài học mới vào khóa học (TEACHER sở hữu khóa học hoặc ADMIN).
     */
    @PostMapping("/courses/{courseId}/lessons")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(
            @PathVariable("courseId") Long courseId,
            @Valid @RequestBody LessonCreateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        LessonResponse response = lessonService.createLesson(courseId, request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Tạo bài học thành công", response));
    }

    /**
     * Cập nhật thông tin bài học (TEACHER sở hữu hoặc ADMIN).
     */
    @PutMapping("/lessons/{lessonId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(
            @PathVariable("lessonId") Long lessonId,
            @RequestBody LessonUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        LessonResponse response = lessonService.updateLesson(lessonId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.success(200, "Cập nhật bài học thành công", response));
    }

    /**
     * Xuất bản (Publish) bài học (TEACHER sở hữu hoặc ADMIN).
     */
    @PutMapping("/lessons/{lessonId}/publish")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<LessonResponse>> publishLesson(
            @PathVariable("lessonId") Long lessonId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        LessonResponse response = lessonService.publishLesson(lessonId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(200, "Bài học đã được xuất bản thành công", response));
    }

    /**
     * Xóa bài học khỏi khóa học (TEACHER sở hữu hoặc ADMIN).
     */
    @DeleteMapping("/lessons/{lessonId}")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(
            @PathVariable("lessonId") Long lessonId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        lessonService.deleteLesson(lessonId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(200, "Xóa bài học thành công", null));
    }

    /**
     * Xem trước nội dung rút gọn của bài học.
     */
    @GetMapping("/lessons/{lessonId}/content_preview")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<LessonResponse>> getContentPreview(
            @PathVariable("lessonId") Long lessonId) {

        LessonResponse response = lessonService.getContentPreview(lessonId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}