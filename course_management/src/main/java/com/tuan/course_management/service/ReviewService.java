package com.tuan.course_management.service;

import com.tuan.course_management.dto.request.ReviewRequest;
import com.tuan.course_management.dto.response.PageResponse;
import com.tuan.course_management.dto.response.ReviewResponse;
import com.tuan.course_management.entity.Course;
import com.tuan.course_management.entity.Review;
import com.tuan.course_management.entity.User;
import com.tuan.course_management.enums.Role;
import com.tuan.course_management.exception.AppException;
import com.tuan.course_management.exception.ErrorCode;
import com.tuan.course_management.mapper.ReviewMapper;
import com.tuan.course_management.repository.CourseRepository;
import com.tuan.course_management.repository.EnrollmentRepository;
import com.tuan.course_management.repository.ReviewRepository;
import com.tuan.course_management.security.UserPrincipal;
import com.tuan.course_management.util.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Dịch vụ xử lý nghiệp vụ liên quan đến đánh giá khóa học.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserService userService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "createdAt", "rating");

    /**
     * Lấy danh sách đánh giá của một khóa học (Đáp ứng STT 40).
     */
    public PageResponse<ReviewResponse> getReviewsByCourse(Long courseId, int page, int size, String sortBy, String sortDir) {
        log.debug("Lấy danh sách đánh giá cho Course ID: {}", courseId);

        if (!courseRepository.existsById(courseId)) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }

        String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";
        Pageable pageable = PageUtils.createPageable(page, size, safeSortBy, sortDir, "createdAt");

        Page<Review> reviewPage = reviewRepository.findByCourseId(courseId, pageable);
        return PageResponse.from(reviewPage.map(ReviewMapper::toResponse));
    }

    /**
     * Gửi đánh giá khóa học (Đáp ứng STT 41) - Chỉ STUDENT đã đăng ký khóa học.
     */
    @Transactional
    public ReviewResponse createReview(Long courseId, ReviewRequest request, UserPrincipal currentUser) {
        Long studentId = currentUser.getId();
        log.debug("Student ID {} gửi đánh giá cho Course ID: {}", studentId, courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (!enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new AppException(ErrorCode.ENROLLMENT_NOT_FOUND);
        }

        if (reviewRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new AppException(ErrorCode.ALREADY_REVIEWED);
        }

        User student = userService.getUserEntityById(studentId);

        Review review = Review.builder()
                .rating(request.getRating())
                .comment(request.getComment())
                .student(student)
                .course(course)
                .build();

        Review saved = reviewRepository.save(review);
        log.info("Tạo đánh giá thành công. Review ID: {}", saved.getId());

        return ReviewMapper.toResponse(saved);
    }

    /**
     * Cập nhật đánh giá (Đáp ứng STT 42) - Chỉ chính chủ hoặc ADMIN.
     */
    @Transactional
    public ReviewResponse updateReview(Long reviewId, ReviewRequest request, UserPrincipal currentUser) {
        log.debug("User ID {} cập nhật đánh giá ID: {}", currentUser.getId(), reviewId);

        Review review = getReviewEntityById(reviewId);
        checkReviewOwnership(review, currentUser);

        if (request.getRating() != null) review.setRating(request.getRating());
        if (request.getComment() != null) review.setComment(request.getComment());

        log.info("Cập nhật đánh giá thành công. Review ID: {}", review.getId());
        return ReviewMapper.toResponse(review);
    }

    /**
     * Xóa đánh giá (Đáp ứng STT 43) - Chỉ chính chủ hoặc ADMIN.
     */
    @Transactional
    public void deleteReview(Long reviewId, UserPrincipal currentUser) {
        log.debug("User ID {} xóa đánh giá ID: {}", currentUser.getId(), reviewId);

        Review review = getReviewEntityById(reviewId);
        checkReviewOwnership(review, currentUser);

        reviewRepository.delete(review);
        log.info("Xóa đánh giá thành công. Review ID: {}", reviewId);
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    public Review getReviewEntityById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new AppException(ErrorCode.REVIEW_NOT_FOUND));
    }

    private void checkReviewOwnership(Review review, UserPrincipal currentUser) {
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwner = review.getStudent().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwner) {
            log.warn("User ID {} không có quyền thao tác trên Review ID {}", currentUser.getId(), review.getId());
            throw new AppException(ErrorCode.REVIEW_ACCESS_DENIED);
        }
    }
}