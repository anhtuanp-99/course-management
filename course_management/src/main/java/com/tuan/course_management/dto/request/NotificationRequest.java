package com.tuan.course_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO dùng cho ADMIN tạo thông báo mới.
 * userId là bắt buộc (thông báo cá nhân).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationRequest {

    @NotBlank(message = "Tiêu đề thông báo không được để trống")
    @Size(max = 200, message = "Tiêu đề không quá 200 ký tự")
    private String title;

    private String content;

    @NotNull(message = "ID người nhận không được để trống")
    private Long userId;
}