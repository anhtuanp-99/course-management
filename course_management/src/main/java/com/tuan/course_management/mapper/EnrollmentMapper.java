package com.tuan.course_management.mapper;

import com.tuan.course_management.dto.response.EnrollmentDetailResponse;
import com.tuan.course_management.dto.response.EnrollmentResponse;
import com.tuan.course_management.dto.response.LessonProgressResponse;
import com.tuan.course_management.entity.Enrollment;
import com.tuan.course_management.entity.LessonProgress;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper chuyển đổi giữa entity Enrollment và các DTO liên quan.
 */
public class EnrollmentMapper {

    /**
     * Chuyển Enrollment entity sang EnrollmentResponse.
     */
    public static EnrollmentResponse toResponse(Enrollment enrollment) {
        if (enrollment == null) return null;

        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudent() != null ? enrollment.getStudent().getId() : null)
                .courseId(enrollment.getCourse() != null ? enrollment.getCourse().getId() : null)
                .courseTitle(enrollment.getCourse() != null ? enrollment.getCourse().getTitle() : null)
                .enrolledAt(enrollment.getEnrolledAt())
                .build();
    }

    /**
     * Chuyển Enrollment entity sang EnrollmentDetailResponse (kèm tiến độ bài học).
     */
    public static EnrollmentDetailResponse toDetailResponse(Enrollment enrollment, List<LessonProgress> progressList) {
        if (enrollment == null) return null;

        List<LessonProgressResponse> progressResponses = progressList != null
                ? progressList.stream()
                  .map(EnrollmentMapper::toProgressResponse)
                  .collect(Collectors.toList())
                : List.of();

        return EnrollmentDetailResponse.builder()
                .id(enrollment.getId())
                .studentId(enrollment.getStudent() != null ? enrollment.getStudent().getId() : null)
                .courseId(enrollment.getCourse() != null ? enrollment.getCourse().getId() : null)
                .courseTitle(enrollment.getCourse() != null ? enrollment.getCourse().getTitle() : null)
                .enrolledAt(enrollment.getEnrolledAt())
                .progress(progressResponses)
                .build();
    }

    /**
     * Chuyển LessonProgress entity sang LessonProgressResponse.
     */
    private static LessonProgressResponse toProgressResponse(LessonProgress progress) {
        if (progress == null) return null;

        return LessonProgressResponse.builder()
                .lessonId(progress.getLesson() != null ? progress.getLesson().getId() : null)
                .lessonTitle(progress.getLesson() != null ? progress.getLesson().getTitle() : null)
                .completed(progress.isCompleted())
                .completedAt(progress.getCompletedAt())
                .build();
    }
}
