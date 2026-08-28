package com.tuan.course_management.mapper;

import com.tuan.course_management.dto.response.UserResponse;
import com.tuan.course_management.entity.User;

/**
 * Mapper chuyển đổi giữa entity User và UserResponse DTO.
 */
public class UserMapper {

    /**
     * Chuyển từ User entity sang UserResponse DTO.
     * Không bao gồm password.
     */
    public static UserResponse toResponse(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

}
