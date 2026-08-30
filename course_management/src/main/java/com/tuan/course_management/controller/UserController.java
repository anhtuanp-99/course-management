package com.tuan.course_management.controller;

import com.tuan.course_management.dto.request.*;
import com.tuan.course_management.dto.response.ApiResponse;
import com.tuan.course_management.dto.response.PageResponse;
import com.tuan.course_management.dto.response.UserResponse;
import com.tuan.course_management.enums.Role;
import com.tuan.course_management.security.UserPrincipal;
import com.tuan.course_management.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller tiếp nhận và xử lý các Endpoint RESTful liên quan đến quản lý người dùng.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Lấy danh sách người dùng có phân trang và bộ lọc (Chỉ ADMIN).
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getUsers(
            @RequestParam(name = "role", required = false) Role role,
            @RequestParam(name = "status", required = false) Boolean isActive,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "sortDir", defaultValue = "desc") String sortDir) {

        PageResponse<UserResponse> response = userService.getUsers(page, size, sortBy, sortDir, role, isActive);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Lấy thông tin chi tiết một người dùng (Chỉ ADMIN).
     * Đáp ứng STT 5.
     */
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(
            @PathVariable("userId") Long userId) {
        UserResponse response = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Tạo tài khoản người dùng mới từ màn hình quản trị (Chỉ ADMIN).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo người dùng thành công", response));
    }

    /**
     * Cập nhật vai trò (role) của người dùng (Chỉ ADMIN).
     * Ràng buộc: ADMIN không được phép sửa role của ADMIN khác hoặc của chính mình.
     */
    @PutMapping("/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserRole(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody UpdateRoleRequest request,
            @AuthenticationPrincipal UserPrincipal currentAdmin) {

        UserResponse response = userService.updateUserRole(userId, request, currentAdmin.getId());
        return ResponseEntity.ok(ApiResponse.success("Cập nhật role thành công", response));
    }

    /**
     * Kích hoạt hoặc vô hiệu hóa tài khoản người dùng (Chỉ ADMIN).
     */
    @PutMapping("/{userId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserStatus(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody UpdateStatusRequest request) {

        UserResponse response = userService.updateUserStatus(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái thành công", response));
    }

    /**
     * Cập nhật thông tin hồ sơ cá nhân (Chỉ OWNER hoặc ADMIN).
     */
    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody UpdateUserRequest request) {

        UserResponse response = userService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật hồ sơ thành công", response));
    }

    /**
     * Đổi mật khẩu tài khoản người dùng (Chỉ OWNER hoặc ADMIN).
     */
    @PutMapping("/{userId}/password")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Đổi mật khẩu thành công", null));
    }

    /**
     * Xóa tài khoản người dùng khỏi hệ thống (Chỉ ADMIN).
     */
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteUser(
            @PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Xóa người dùng thành công", null));
    }
}