package com.tuan.course_management.service;

import com.tuan.course_management.dto.request.LoginRequest;
import com.tuan.course_management.dto.response.ApiResponse;
import com.tuan.course_management.dto.response.JwtResponse;
import com.tuan.course_management.dto.response.UserResponse;
import com.tuan.course_management.entity.User;
import com.tuan.course_management.mapper.UserMapper;
import com.tuan.course_management.repository.UserRepository;
import com.tuan.course_management.security.JwtProvider;
import com.tuan.course_management.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Dịch vụ xử lý nghiệp vụ xác thực người dùng và quản lý Token.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    /**
     * Xác thực thông tin đăng nhập và khởi tạo chuỗi Token.
     *
     * @param request Thông tin email và password từ Client
     * @return JwtResponse Chứa Access Token, Refresh Token và thông tin cơ bản
     */
    public JwtResponse login(LoginRequest request) {
        log.debug("Bắt đầu xác thực đăng nhập cho email: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        String accessToken = jwtProvider.generateAccessToken(userPrincipal);
        String refreshToken = jwtProvider.generateRefreshToken(userPrincipal);

        log.info("Đăng nhập thành công cho UserID: {}, Email: {}", userPrincipal.getId(), userPrincipal.getUsername());

        return JwtResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(userPrincipal.getId())
                .email(userPrincipal.getUsername())
                .fullName(userPrincipal.getFullName())
                .role(userPrincipal.getRole())
                .build();
    }

    /**
     * Lấy thông tin chi tiết của người dùng đang đăng nhập dựa trên SecurityContext.
     *
     * @param userPrincipal Thông tin người dùng đã qua bộ lọc Security
     * @return UserResponse Thông tin chi tiết người dùng
     */
    @Transactional(readOnly = true)
    public UserResponse getMe(UserPrincipal userPrincipal) {
        log.debug("Lấy thông tin người dùng hiện tại có ID: {}", userPrincipal.getId());

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng"));

        return UserMapper.toResponse(user);
    }

    /**
     * Kiểm tra tính hợp lệ của Token.
     *
     * @param token Chuỗi JWT Token cần kiểm tra
     * @return ApiResponse Trạng thái hợp lệ của Token
     */
    public ApiResponse<Void> verify(String token) {
        if (token == null || token.isBlank()) {
            return ApiResponse.error("Token không được để trống");
        }

        boolean isValid = jwtProvider.validateToken(token);
        if (isValid) {
            return ApiResponse.success("Token hợp lệ", null);
        }
        return ApiResponse.error("Token không hợp lệ hoặc đã hết hạn");
    }

    /**
     * Đăng xuất người dùng khỏi hệ thống.
     *
     * @return ApiResponse Thông báo đăng xuất thành công
     */
    public ApiResponse<Void> logout() {
        log.info("Xử lý đăng xuất người dùng thành công");
        return ApiResponse.success("Đăng xuất thành công", null);
    }
}