package com.tuan.course_management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO dùng để cập nhật thông tin cá nhân của người dùng (OWNER/ADMIN).
 * Chỉ cho phép cập nhật họ tên, email, số điện thoại.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateUserRequest {

    @Size(max = 100, message = "Họ tên không quá 100 kí tự")
    private String fullName;

    @Email(message = "Email không đúng định dạng")
    private String email;

    @Size(max = 20, message = "Số điện thoại không quá 20 ký tự")
    private String phone;
}
