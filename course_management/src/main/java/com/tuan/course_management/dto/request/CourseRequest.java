package com.tuan.course_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO dùng khi tạo mới hoặc cập nhật khóa học (ADMIN).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequest {

    @NotBlank(message = "Tiêu đề khóa học không được để trống")
    @Size(max = 200, message = "Tiêu đề không quá 200 ký tự")
    private String title;

    private String description;

    /**
     * ID của giảng viên phụ trách khóa học.
     * Bắt buộc khi tạo mới.
     */
    @NotNull(message = "Giảng viên không được để trống")
    private Long teacherId;
}
