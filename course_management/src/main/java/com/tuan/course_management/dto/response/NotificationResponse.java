package com.tuan.course_management.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;
    private Long userId;
    private String message;
    private String type;
    private String targetUrl;
    private boolean read;
    private LocalDateTime createdAt;
}