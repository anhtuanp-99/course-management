package com.tuan.course_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationCreateRequest {

    @NotNull(message = "ID người nhận thông báo không được để trống")
    private Long userId;

    @NotBlank(message = "Nội dung thông báo không được để trống")
    private String message;

    private String type;
    private String targetUrl;
}