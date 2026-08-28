package com.tuan.course_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO dùng để cập nhật trạng thái hoạt động (isActive) của người dùng.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStatusRequest {

    @NotNull(message = "Trạng thái active không được để trống")
    private Boolean active;
}
