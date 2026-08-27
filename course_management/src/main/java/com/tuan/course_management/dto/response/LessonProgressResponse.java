package com.tuan.course_management.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO trả về tiến độ của một bài học trong enrollment.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonProgressResponse {

    private Long id;
    private Long enrollmentId;
    private Long lessonId;
    private boolean completed;
    private LocalDateTime completedAt;
    private LocalDateTime updatedAt;

}
