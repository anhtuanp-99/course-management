package com.tuan.course_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO dùng khi học viên đăng ký khóa học.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentRequest {

    @NotNull(message = "ID khóa học không được để trống")
    private Long id;
}
