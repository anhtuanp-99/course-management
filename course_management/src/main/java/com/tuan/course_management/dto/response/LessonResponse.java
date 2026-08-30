package com.tuan.course_management.dto.response;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO trả về thông tin bài học
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonResponse {

    private Long id;
    private String title;
    private String content;
    private boolean isPublished;
    private Long courseId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
