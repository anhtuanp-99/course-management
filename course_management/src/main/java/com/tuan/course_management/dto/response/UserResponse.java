package com.tuan.course_management.dto.response;

import com.tuan.course_management.enums.Role;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO trả về thông tin người dùng an toàn (không bao gồm password).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}