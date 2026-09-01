package com.tuan.course_management.mapper;

import com.tuan.course_management.dto.response.CourseSummaryResponse;
import com.tuan.course_management.dto.response.EnrollmentResponse;
import com.tuan.course_management.dto.response.UserSummaryResponse;
import com.tuan.course_management.entity.Course;
import com.tuan.course_management.entity.Enrollment;
import com.tuan.course_management.entity.User;
import com.tuan.course_management.enums.EnrollmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnrollmentMapper {

    private final UserMapper userMapper;
    private final CourseMapper courseMapper;

    public Enrollment toEntity(User student, Course course) {
        if (student == null || course == null) return null;

        return Enrollment.builder()
                .student(student)
                .course(course)
                .status(EnrollmentStatus.ENROLLED)
                .progressPercentage(0.00)
                .build();
    }

    public EnrollmentResponse toResponse(Enrollment entity, Double avgRating, Long totalStudents) {
        if (entity == null) return null;

        UserSummaryResponse studentSummary = userMapper.toSummaryResponse(entity.getStudent());
        CourseSummaryResponse courseSummary = courseMapper.toSummaryResponse(
                entity.getCourse(), avgRating, totalStudents
        );

        return EnrollmentResponse.builder()
                .id(entity.getId())
                .student(studentSummary)
                .course(courseSummary)
                .status(entity.getStatus())
                .progressPercentage(entity.getProgressPercentage())
                .enrolledAt(entity.getEnrolledAt())
                .completionDate(entity.getCompletionDate())
                .build();
    }
}