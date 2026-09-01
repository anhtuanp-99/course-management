package com.tuan.course_management.mapper;

import com.tuan.course_management.dto.request.NotificationCreateRequest;
import com.tuan.course_management.dto.response.NotificationResponse;
import com.tuan.course_management.entity.Notification;
import com.tuan.course_management.entity.User;
import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public Notification toEntity(NotificationCreateRequest request, User user) {
        if (request == null || user == null) return null;

        return Notification.builder()
                .user(user)
                .message(request.getMessage())
                .type(request.getType())
                .targetUrl(request.getTargetUrl())
                .read(false)
                .build();
    }

    public NotificationResponse toResponse(Notification entity) {
        if (entity == null) return null;

        return NotificationResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .message(entity.getMessage())
                .type(entity.getType())
                .targetUrl(entity.getTargetUrl())
                .read(entity.isRead())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}