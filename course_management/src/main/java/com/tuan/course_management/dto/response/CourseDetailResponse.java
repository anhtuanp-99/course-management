package com.tuan.course_management.dto.response;

import com.tuan.course_management.enums.CourseStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO trả về chi tiết khóa học, kèm danh sách bài học đã publish.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDetailResponse {

    private Long id;
    private String title;
    private String description;
    private Long teacherId;    // ID của giảng viên phụ trách
    private String teacherName; // Tên giảng viên (đã map)
    private CourseStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<LessonResponse> lessons;

}
