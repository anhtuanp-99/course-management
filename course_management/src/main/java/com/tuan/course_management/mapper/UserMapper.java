package com.tuan.course_management.mapper;

import com.tuan.course_management.dto.request.RegisterRequest;
import com.tuan.course_management.dto.request.UserCreateRequest;
import com.tuan.course_management.dto.response.UserResponse;
import com.tuan.course_management.dto.response.UserSummaryResponse;
import com.tuan.course_management.entity.User;
import com.tuan.course_management.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(RegisterRequest request) {
        if (request == null) return null;

        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .role(Role.STUDENT)
                .active(true)
                .build();
    }

    public User toEntity(UserCreateRequest request) {
        if (request == null) return null;

        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .role(request.getRole() != null ? request.getRole() : Role.STUDENT)
                .phone(request.getPhone())
                .active(true)
                .build();
    }

    public UserResponse toResponse(User entity) {
        if (entity == null) return null;

        return UserResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .fullName(entity.getFullName())
                .role(entity.getRole())
                .phone(entity.getPhone())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public UserSummaryResponse toSummaryResponse(User entity) {
        if (entity == null) return null;

        return UserSummaryResponse.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .role(entity.getRole())
                .build();
    }
}