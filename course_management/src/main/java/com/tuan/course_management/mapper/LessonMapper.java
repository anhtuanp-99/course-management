package com.tuan.course_management.mapper;

import com.tuan.course_management.dto.request.LessonCreateRequest;
import com.tuan.course_management.dto.response.LessonResponse;
import com.tuan.course_management.dto.response.LessonSummaryResponse;
import com.tuan.course_management.entity.Course;
import com.tuan.course_management.entity.Lesson;
import org.springframework.stereotype.Component;

@Component
public class LessonMapper {

    public Lesson toEntity(LessonCreateRequest request, Course course) {
        if (request == null) return null;

        return Lesson.builder()
                .course(course)
                .title(request.getTitle())
                .contentUrl(request.getContentUrl())
                .textContent(request.getTextContent())
                .orderIndex(request.getOrderIndex())
                .published(request.isPublished())
                .build();
    }

    public LessonResponse toResponse(Lesson entity) {
        if (entity == null) return null;

        return LessonResponse.builder()
                .id(entity.getId())
                .courseId(entity.getCourse() != null ? entity.getCourse().getId() : null)
                .title(entity.getTitle())
                .contentUrl(entity.getContentUrl())
                .textContent(entity.getTextContent())
                .orderIndex(entity.getOrderIndex())
                .published(entity.isPublished())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public LessonSummaryResponse toSummaryResponse(Lesson entity) {
        if (entity == null) return null;

        return LessonSummaryResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .orderIndex(entity.getOrderIndex())
                .published(entity.isPublished())
                .build();
    }
}