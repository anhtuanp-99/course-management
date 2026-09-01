package com.tuan.course_management.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonProgressResponse {

    private Long id;
    private Long enrollmentId;
    private Long lessonId;
    private String lessonTitle;
    private boolean completed;
    private LocalDateTime completedAt;
    private LocalDateTime lastAccessedAt;
}