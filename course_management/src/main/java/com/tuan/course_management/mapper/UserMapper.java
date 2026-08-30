package com.tuan.course_management.mapper;

import com.tuan.course_management.dto.response.UserResponse;
import com.tuan.course_management.entity.User;
import lombok.experimental.UtilityClass;

/**
 * Class tiện ích chuyển đổi giữa Entity User và UserResponse DTO.
 */
@UtilityClass
public class UserMapper {

    /**
     * Chuyển từ User entity sang UserResponse DTO.
     */
    public static UserResponse toResponse(User user) {
        if (user == null) {
            return null;
        }

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