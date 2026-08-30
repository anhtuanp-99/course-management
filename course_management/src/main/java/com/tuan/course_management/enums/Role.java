package com.tuan.course_management.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum định nghĩa các vai trò phân quyền người dùng trong hệ thống.
 */
@Getter
@RequiredArgsConstructor
public enum Role {

    ADMIN("Quản trị viên"),
    TEACHER("Giảng viên"),
    STUDENT("Học viên");

    private final String description;

    /**
     * Trả về tên vai trò kèm tiền tố 'ROLE_' chuẩn hóa theo định dạng Spring Security.
     */
    public String getAuthority() {
        return "ROLE_" + this.name();
    }
}