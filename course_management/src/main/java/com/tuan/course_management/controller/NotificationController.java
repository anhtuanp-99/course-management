package com.tuan.course_management.controller;

import com.tuan.course_management.dto.request.NotificationRequest;
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
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Lấy danh sách thông báo của người dùng hiện tại (Yêu cầu đã đăng nhập).
     * Đáp ứng STT 33.
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<PageResponse<NotificationResponse>>> getNotifications(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir) {

        PageResponse<NotificationResponse> response = notificationService.getNotifications(
                currentUser, page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Đánh dấu thông báo đã đọc (Yêu cầu chính chủ thông báo).
     * Đáp ứng STT 34.
     */
    @PutMapping("/{notificationId}/read")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable("notificationId") Long notificationId,
            @AuthenticationPrincipal UserPrincipal currentUser) {

        notificationService.markAsRead(notificationId, currentUser);
        return ResponseEntity.ok(ApiResponse.success("Đã đánh dấu thông báo là đã đọc", null));
    }

    /**
     * Tạo thông báo mới cho người dùng (Chỉ ADMIN).
     * Trả về HTTP Status 201 CREATED theo chuẩn RESTful API.
     * Đáp ứng STT 35.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(
            @Valid @RequestBody NotificationRequest request) {

        NotificationResponse response = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo thông báo thành công", response));
    }

    /**
     * Xóa thông báo khỏi hệ thống (Chỉ ADMIN).
     * Đáp ứng STT 36.
     */
    @DeleteMapping("/{notificationId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @PathVariable("notificationId") Long notificationId) {

        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok(ApiResponse.success("Xóa thông báo thành công", null));
    }
}