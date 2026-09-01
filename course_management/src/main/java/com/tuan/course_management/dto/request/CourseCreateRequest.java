package com.tuan.course_management.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseCreateRequest {

    @NotBlank(message = "Tiêu đề khóa học không được để trống")
    @Size(max = 200, message = "Tiêu đề không quá 200 ký tự")
    private String title;

    private String description;

    @NotNull(message = "Giá khóa học không được để trống")
    @DecimalMin(value = "0.0", message = "Giá khóa học phải lớn hơn hoặc bằng 0")
    private BigDecimal price;

    @Min(value = 1, message = "Thời lượng khóa học phải tối thiểu 1 giờ")
    private Integer durationHours;

    @NotNull(message = "ID giảng viên phụ trách không được để trống")
    private Long teacherId;
}