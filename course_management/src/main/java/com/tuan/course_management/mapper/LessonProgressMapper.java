package com.tuan.course_management.mapper;

import com.tuan.course_management.dto.response.LessonProgressResponse;
import com.tuan.course_management.entity.LessonProgress;
import org.springframework.stereotype.Component;

@Component
public class LessonProgressMapper {

    public LessonProgressResponse toResponse(LessonProgress entity) {
        if (entity == null) return null;

        return LessonProgressResponse.builder()
                .id(entity.getId())
                .enrollmentId(entity.getEnrollment() != null ? entity.getEnrollment().getId() : null)
                .lessonId(entity.getLesson() != null ? entity.getLesson().getId() : null)
                .lessonTitle(entity.getLesson() != null ? entity.getLesson().getTitle() : null)
                .completed(entity.isCompleted())
                .completedAt(entity.getCompletedAt())
                .lastAccessedAt(entity.getLastAccessedAt())
                .build();
    }
}