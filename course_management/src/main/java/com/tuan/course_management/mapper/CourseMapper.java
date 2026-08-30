package com.tuan.course_management.mapper;

import com.tuan.course_management.dto.response.CourseDetailResponse;
import com.tuan.course_management.dto.response.CourseResponse;
import com.tuan.course_management.dto.response.LessonResponse;
import com.tuan.course_management.entity.Course;
import com.tuan.course_management.entity.Lesson;
import lombok.experimental.UtilityClass;

import java.util.List;

/**
 * Class tiện ích chuyển đổi giữa Entity Course và các DTO phản hồi liên quan.
 */
@UtilityClass
public class CourseMapper {

    /**
     * Chuyển Course entity sang CourseResponse DTO cơ bản.
     */
    public static CourseResponse toResponse(Course course) {
        if (course == null) {
            return null;
        }

        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .teacherId(course.getTeacher() != null ? course.getTeacher().getId() : null)
                .teacherName(course.getTeacher() != null ? course.getTeacher().getFullName() : null)
                .status(course.getStatus())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .build();
    }

    /**
     * Chuyển Course entity sang CourseDetailResponse DTO kèm danh sách bài học đã xuất bản.
     */
    public static CourseDetailResponse toDetailResponse(Course course, List<Lesson> publishedLessons) {
        if (course == null) {
            return null;
        }

        List<LessonResponse> lessonResponses = publishedLessons != null
                ? publishedLessons.stream()
                  .map(LessonMapper::toResponse)
                  .toList()
                : List.of();

        return CourseDetailResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .teacherId(course.getTeacher() != null ? course.getTeacher().getId() : null)
                .teacherName(course.getTeacher() != null ? course.getTeacher().getFullName() : null)
                .status(course.getStatus())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .lessons(lessonResponses)
                .build();
    }
}