package com.tuan.course_management.controller;

import com.tuan.course_management.dto.request.NotificationCreateRequest;
import com.tuan.course_management.dto.response.ApiResponse;
import com.tuan.course_management.dto.response.NotificationResponse;
import com.tuan.course_management.dto.response.PageResponse;
import com.tuan.course_management.security.UserPrincipal;
import com.tuan.course_management.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller tiếp nhận và xử lý các Endpoint RESTful liên quan đến thông báo.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Lấy danh sách thông báo của người dùng hiện tại (Yêu cầu đã đăng nhập).
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> getNotifications(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir) {

        PageResponse<NotificationResponse> response = notificationService.getNotifications(
                currentUser, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Đánh dấu thông báo là đã đọc (Yêu cầu chính chủ thông báo).
     */
    @PutMapping("/{notificationId}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @PathVariable("notificationId") Long notificationId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        NotificationResponse response = notificationService.markAsRead(notificationId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(200, "Đã đánh dấu thông báo là đã đọc", response));
    }

    /**
     * Tạo thông báo mới cho người dùng (Chỉ ADMIN).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(
            @Valid @RequestBody NotificationCreateRequest request) {

        NotificationResponse response = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(201, "Tạo thông báo thành công", response));
    }

    /**
     * Xóa thông báo khỏi hệ thống (Chỉ ADMIN).
     */
    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @PathVariable("notificationId") Long notificationId) {

        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok(ApiResponse.success(200, "Xóa thông báo thành công", null));
    }
}