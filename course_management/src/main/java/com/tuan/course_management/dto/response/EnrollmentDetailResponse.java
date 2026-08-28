package com.tuan.course_management.dto.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO trả về chi tiết đăng ký khóa học, kèm tiến độ từng bài học.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnrollmentDetailResponse {

    private Long id;
    private Long studentId;
    private Long courseId;
    private String courseTitle;
    private LocalDateTime enrolledAt;
    private List<LessonProgressResponse> progress; // danh sách tiến độ bài học
}