package com.tuan.course_management.dto.response;

import com.tuan.course_management.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private Role role;
    private String phone;

    /**
     * Dùng primitive boolean cho Response hiển thị trạng thái bắt buộc của User.
     */
    private boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}