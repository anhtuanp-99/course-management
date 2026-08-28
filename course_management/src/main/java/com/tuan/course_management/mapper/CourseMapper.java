package com.tuan.course_management.mapper;

import com.tuan.course_management.dto.response.CourseDetailResponse;
import com.tuan.course_management.dto.response.CourseResponse;
import com.tuan.course_management.dto.response.LessonResponse;
import com.tuan.course_management.entity.Course;
import com.tuan.course_management.entity.Lesson;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper chuyển đổi giữa entity Course và các DTO liên quan.
 */
public class CourseMapper {

    /**
     * Chuyển Course entity sang CourseResponse (không kèm bài học).
     */
    public static CourseResponse toResponse(Course course) {
        if (course == null) return null;

        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .teacher_id(course.getTeacher() != null ? course.getTeacher().getId() : null)
                .teacherName(course.getTeacher() != null ? course.getTeacher().getFullName() : null)
                .status(course.getStatus())
                .createAt(course.getCreatedAt())
                .updateAt(course.getUpdatedAt())
                .build();
    }

    /**
     * Chuyển Course entity sang CourseDetailResponse (kèm danh sách bài học đã publish).
     */
    public static CourseDetailResponse toDetailResponse(Course course, List<Lesson> publishedLesson) {
        if (course == null) return null;

        List<LessonResponse> lessonResponses = publishedLesson != null
                ? publishedLesson.stream()
                  .map(LessonMapper::toResponse)
                  .collect(Collectors.toList())
                : List.of();

        return CourseDetailResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .teacher_id(course.getTeacher() != null ? course.getTeacher().getId() : null)
                .teacherName(course.getTeacher() != null ? course.getTeacher().getFullName() : null)
                .status(course.getStatus())
                .createAt(course.getCreatedAt())
                .updateAt(course.getUpdatedAt())
                .lessons(lessonResponses)
                .build();
    }
}
