package com.tuan.course_management.mapper;

import com.tuan.course_management.dto.request.CourseCreateRequest;
import com.tuan.course_management.dto.response.CourseDetailResponse;
import com.tuan.course_management.dto.response.CourseSummaryResponse;
import com.tuan.course_management.entity.Course;
import com.tuan.course_management.entity.User;
import com.tuan.course_management.enums.CourseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class CourseMapper {

    private final UserMapper userMapper;

    public Course toEntity(CourseCreateRequest request, User teacher) {
        if (request == null) return null;

        return Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice() != null ? request.getPrice() : BigDecimal.ZERO)
                .durationHours(request.getDurationHours())
                .teacher(teacher)
                .status(CourseStatus.DRAFT)
                .build();
    }

    public CourseSummaryResponse toSummaryResponse(Course entity, Double avgRating, Long totalStudents) {
        if (entity == null) return null;

        return CourseSummaryResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .price(entity.getPrice())
                .durationHours(entity.getDurationHours())
                .status(entity.getStatus())
                .teacherName(entity.getTeacher() != null ? entity.getTeacher().getFullName() : null)
                .avgRating(avgRating != null ? avgRating : 0.0)
                .totalStudents(totalStudents != null ? totalStudents : 0L)
                .build();
    }

    public CourseDetailResponse toDetailResponse(Course entity, Double avgRating, Long totalStudents, Long totalLessons) {
        if (entity == null) return null;

        return CourseDetailResponse.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .durationHours(entity.getDurationHours())
                .status(entity.getStatus())
                .teacher(userMapper.toSummaryResponse(entity.getTeacher()))
                .avgRating(avgRating != null ? avgRating : 0.0)
                .totalStudents(totalStudents != null ? totalStudents : 0L)
                .totalLessons(totalLessons != null ? totalLessons : 0L)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}