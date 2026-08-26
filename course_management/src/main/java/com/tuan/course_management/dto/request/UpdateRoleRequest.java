package com.tuan.course_management.dto.request;

import com.tuan.course_management.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO dùng cho ADMIN cập nhật vai trò  người dùng
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateRoleRequest {

    @NotNull(message = "Vai trò không được để trống")
    private Role role;
}
