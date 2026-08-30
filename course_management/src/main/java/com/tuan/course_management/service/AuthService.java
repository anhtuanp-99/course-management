package com.tuan.course_management.service;

import com.tuan.course_management.dto.request.LoginRequest;
import com.tuan.course_management.dto.response.ApiResponse;
import com.tuan.course_management.dto.response.JwtResponse;
import com.tuan.course_management.dto.response.UserResponse;
import com.tuan.course_management.entity.User;
import com.tuan.course_management.exception.AppException;
import com.tuan.course_management.exception.ErrorCode;
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
 * Dịch vụ xử lý nghiệp vụ xác thực người dùng và quản lý phiên đăng nhập.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    /**
     * Xác thực thông tin tài khoản và cấp phát chuỗi Token xác thực.
     *
     * @param request Yêu cầu đăng nhập chứa email và password
     * @return JwtResponse Chuỗi Access Token, Refresh Token và thông tin cơ bản
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
     * Kiểm tra trạng thái và tính hợp lệ của JWT Token.
     *
     * @param token Chuỗi Token cần xác thực
     * @return ApiResponse Kết quả kiểm tra
     */
    public ApiResponse<Void> verify(String token) {
        log.debug("Kiểm tra tính hợp lệ của Token");

        if (token == null || token.isBlank()) {
            log.warn("Token không được cung cấp");
            return ApiResponse.error("Token không hợp lệ");
        }

        boolean isValid = jwtProvider.validateToken(token);
        if (isValid) {
            log.info("Token hợp lệ");
            return ApiResponse.success("Token hợp lệ", null);
        } else {
            log.warn("Token không hợp lệ hoặc đã hết hạn");
            return ApiResponse.error("Token không hợp lệ hoặc đã hết hạn");
        }
    }

    /**
     * Truy vấn thông tin chi tiết của người dùng đang đăng nhập dựa trên thông tin SecurityContext.
     *
     * @param userPrincipal Đối tượng chứa thông tin xác thực đã qua kiểm tra
     * @return UserResponse Dữ liệu người dùng dạng DTO
     */
    @Transactional(readOnly = true)
    public UserResponse getMe(UserPrincipal userPrincipal) {
        log.debug("Lấy thông tin tài khoản cho User ID: {}", userPrincipal.getId());

        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        log.info("Lấy thông tin người dùng thành công cho User ID: {}", user.getId());
        return UserMapper.toResponse(user);
    }

    /**
     * Đăng xuất người dùng khỏi hệ thống.
     *
     * @return ApiResponse Thông báo kết quả đăng xuất
     */
    public ApiResponse<Void> logout() {
        log.info("Thực hiện đăng xuất người dùng thành công");
        return ApiResponse.success("Đăng xuất thành công", null);
    }
}