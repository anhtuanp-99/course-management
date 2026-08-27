package com.tuan.course_management.dto.response.report;

import lombok.*;

/**
 * DTO cho báo cáo top khóa học phổ biến.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopCourseResponse {

    private Long courseId;
    private String courseTitle;
    private long enrollmentCount;   // số lượng học viên đăng ký
    private double averageRating;   // điểm đánh giá trung bình (nếu có)
}