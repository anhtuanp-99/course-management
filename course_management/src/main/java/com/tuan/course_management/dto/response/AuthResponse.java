package com.tuan.course_management.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;

    private String refreshToken; // Bổ sung trường này

    @Builder.Default
    private String tokenType = "Bearer";

    private long expiresIn;

    private UserSummaryResponse user;
}