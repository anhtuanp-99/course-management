package com.tuan.course_management.dto.request;

import com.tuan.course_management.enums.CourseStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseUpdateRequest {

    @Size(max = 200, message = "Tiêu đề không quá 200 ký tự")
    private String title;

    private String description;

    @DecimalMin(value = "0.0", message = "Giá khóa học phải lớn hơn hoặc bằng 0")
    private BigDecimal price;

    @Min(value = 1, message = "Thời lượng khóa học phải tối thiểu 1 giờ")
    private Integer durationHours;

    private CourseStatus status;

    private Long teacherId;
}