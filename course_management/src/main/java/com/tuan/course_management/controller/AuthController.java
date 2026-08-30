package com.tuan.course_management.controller;

import com.tuan.course_management.dto.request.LoginRequest;
import com.tuan.course_management.dto.request.RegisterRequest;
import com.tuan.course_management.dto.response.ApiResponse;
import com.tuan.course_management.dto.response.JwtResponse;
import com.tuan.course_management.dto.response.UserResponse;
import com.tuan.course_management.security.UserPrincipal;
import com.tuan.course_management.service.AuthService;
import com.tuan.course_management.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller tiếp nhận và xử lý các Endpoint RESTful liên quan đến xác thực và tài khoản.
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    /**
     * Đăng ký tài khoản người dùng mới. Trả về HTTP Status 201 CREATED.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Nhận yêu cầu đăng ký tài khoản cho email: {}", request.getEmail());
        userService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Đăng ký tài khoản thành công!", null));
    }

    /**
     * Đăng nhập hệ thống và nhận chuỗi Authentication Token (STT 1).
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest request) {
        log.info("Nhận yêu cầu đăng nhập cho email: {}", request.getEmail());
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", response));
    }

    /**
     * Lấy thông tin tài khoản cá nhân của người dùng hiện tại (STT 3).
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<UserResponse>> me(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserResponse response = authService.getMe(userPrincipal);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * Kiểm tra tính hợp lệ của Token (STT 2).
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verify(@RequestParam(name = "token") String token) {
        authService.verify(token);
        return ResponseEntity.ok(ApiResponse.success("Token hợp lệ", null));
    }

    /**
     * Đăng xuất khỏi hệ thống (STT 30).
     */
    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> logout() {
        authService.logout();
        return ResponseEntity.ok(ApiResponse.success("Đăng xuất thành công", null));
    }
}