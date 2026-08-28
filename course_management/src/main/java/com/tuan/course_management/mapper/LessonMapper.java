package com.tuan.course_management.mapper;

import com.tuan.course_management.dto.response.LessonResponse;
import com.tuan.course_management.entity.Lesson;

/**
 * Mapper chuyển đổi giữa entity Lesson và LessonResponse DTO.
 */
public class LessonMapper {

    /**
     * Chuyển Lesson entity sang LessonResponse.
     */
    public static LessonResponse toResponse(Lesson lesson) {
        if (lesson == null) return null;

        return LessonResponse.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .isPublished(lesson.isPublished())
                .courseId(lesson.getCourse() != null ? lesson.getCourse().getId() : null)
                .createAt(lesson.getCreatedAt())
                .updateAt(lesson.getUpdatedAt())
                .build();
    }
}
