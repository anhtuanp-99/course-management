package com.tuan.course_management.dto.response;

import lombok.*;

/**
 * Response trả về sau khi đăng nhập thành công.
 * Chứa cặp token (Access/Refresh) và thông tin cơ bản của người dùng.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtResponse {

    private String accessToken;
    private String refreshToken;

    @Builder.Default
    private String tokenType = "Bearer";

    // Thông tin người dùng cần thiết cho Frontend hiển thị UI
    private Long userId;
    private String email;
    private String fullName;
    private String role;
}