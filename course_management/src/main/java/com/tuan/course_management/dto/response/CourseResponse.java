package com.tuan.course_management.dto.response;

import com.tuan.course_management.enums.CourseStatus;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO trả về thông tin khóa học cơ bản (không kèm bài học).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseResponse {

    private Long id;
    private String title;
    private String description;
    private Long teacher_id;    // ID của giảng viên phụ trách
    private String teacherName; // Tên giảng viên (đã map)
    private CourseStatus status;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;


}
