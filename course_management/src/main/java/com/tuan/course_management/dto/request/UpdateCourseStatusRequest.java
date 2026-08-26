package com.tuan.course_management.dto.request;

import com.tuan.course_management.enums.CourseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO dùng để cập nhật trạng thái khóa học
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCourseStatusRequest {

    @NotNull(message = "Trạng thái khóa học không được để trống")
    private CourseStatus status;
}
