package com.tuan.course_management.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO trả về thông tin đăng ký khóa học.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentResponse {

    private Long id;
    private Long studentId;
    private Long courseId;
    private String courseTitle;   // tên khóa học (đã map)
    private LocalDateTime enrolledAt;
}
