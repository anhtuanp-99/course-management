package com.tuan.course_management.mapper;

import com.tuan.course_management.dto.request.ReviewCreateRequest;
import com.tuan.course_management.dto.response.ReviewResponse;
import com.tuan.course_management.entity.Course;
import com.tuan.course_management.entity.Review;
import com.tuan.course_management.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewMapper {

    private final UserMapper userMapper;

    public Review toEntity(ReviewCreateRequest request, Course course, User student) {
        if (request == null || course == null || student == null) return null;

        return Review.builder()
                .course(course)
                .student(student)
                .rating(request.getRating())
                .comment(request.getComment() != null ? request.getComment().trim() : null)
                .build();
    }

    public ReviewResponse toResponse(Review entity) {
        if (entity == null) return null;

        return ReviewResponse.builder()
                .id(entity.getId())
                .courseId(entity.getCourse() != null ? entity.getCourse().getId() : null)
                .student(userMapper.toSummaryResponse(entity.getStudent()))
                .rating(entity.getRating())
                .comment(entity.getComment())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}