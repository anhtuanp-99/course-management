package com.tuan.course_management.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentCreateRequest {

    @NotNull(message = "ID khóa học không được để trống")
    private Long courseId;
}