package com.tuan.course_management.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO trả về thông tin thông báo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationResponse {

    private Long id;
    private String title;
    private String content;
    private Long userId;       // null nếu là thông báo chung
    private boolean isRead;
    private LocalDateTime createdAt;
}