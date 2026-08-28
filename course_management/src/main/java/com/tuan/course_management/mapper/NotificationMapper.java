package com.tuan.course_management.mapper;

import com.tuan.course_management.dto.response.NotificationResponse;
import com.tuan.course_management.entity.Notification;

/**
 * Mapper chuyển đổi giữa entity Notification và NotificationResponse DTO.
 */
public class NotificationMapper {

    /**
     * Chuyển Notification entity sang NotificationResponse.
     */
    public static NotificationResponse toResponse(Notification notification) {
        if (notification == null) return null;

        return NotificationResponse.builder()
                .id(notification.getId())
                .title(notification.getTitle())
                .content(notification.getContent())
                .userId(notification.getUser() != null ? notification.getUser().getId() : null)
                .read(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}