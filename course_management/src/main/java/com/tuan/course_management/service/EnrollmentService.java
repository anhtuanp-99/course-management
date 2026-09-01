package com.tuan.course_management.service;

import com.tuan.course_management.dto.request.EnrollmentCreateRequest;
import com.tuan.course_management.dto.response.EnrollmentResponse;
import com.tuan.course_management.dto.response.LessonProgressResponse;
import com.tuan.course_management.dto.response.PageResponse;
import com.tuan.course_management.entity.*;
import com.tuan.course_management.enums.CourseStatus;
import com.tuan.course_management.enums.EnrollmentStatus;
import com.tuan.course_management.enums.Role;
import com.tuan.course_management.exception.AppException;
import com.tuan.course_management.exception.ErrorCode;
import com.tuan.course_management.mapper.EnrollmentMapper;
import com.tuan.course_management.mapper.LessonProgressMapper;
import com.tuan.course_management.repository.*;
import com.tuan.course_management.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Dịch vụ xử lý nghiệp vụ đăng ký khóa học và quản lý tiến độ học tập.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final EnrollmentMapper enrollmentMapper;
    private final LessonProgressMapper lessonProgressMapper;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "enrolledAt", "progressPercentage"
    );

    /**
     * Lấy danh sách các khóa học sinh viên đã đăng ký (STT 22).
     */
    public PageResponse<EnrollmentResponse> getEnrollments(UserPrincipal currentUser,
                                                           int page, int size, String sortBy, String sortDir) {
        Long studentId = currentUser.getId();
        log.debug("Lấy danh sách đăng ký cho Student ID: {}", studentId);

        String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "enrolledAt";
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(safeSortBy).ascending() : Sort.by(safeSortBy).descending();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, sort);

        Page<Enrollment> enrollmentPage = enrollmentRepository.findByStudentId(studentId, pageable);
        List<EnrollmentResponse> mappedContent = enrollmentPage.getContent().stream()
                .map(enrollment -> {
                    Long courseId = enrollment.getCourse().getId();
                    Double avgRating = reviewRepository.calculateAvgRatingByCourseId(courseId);
                    long totalStudents = enrollmentRepository.countByCourseId(courseId);
                    return enrollmentMapper.toResponse(enrollment, avgRating, totalStudents);
                })
                .toList();

        return PageResponse.from(enrollmentPage, mappedContent);
    }

    /**
     * Đăng ký một khóa học mới (STT 23).
     */
    @Transactional
    public EnrollmentResponse enroll(EnrollmentCreateRequest request, UserPrincipal currentUser) {
        Long studentId = currentUser.getId();
        Long courseId = request.getCourseId();
        log.debug("Student ID {} thực hiện đăng ký Course ID: {}", studentId, courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new AppException(ErrorCode.COURSE_NOT_PUBLISHED_FOR_ENROLL);
        }

        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new AppException(ErrorCode.ALREADY_ENROLLED);
        }

        User student = userService.getUserEntityById(studentId);

        Enrollment enrollment = enrollmentMapper.toEntity(student, course);
        Enrollment saved = enrollmentRepository.save(enrollment);

        Double avgRating = reviewRepository.calculateAvgRatingByCourseId(courseId);
        long totalStudents = enrollmentRepository.countByCourseId(courseId);

        log.info("Đăng ký khóa học thành công. Enrollment ID: {}", saved.getId());
        return enrollmentMapper.toResponse(saved, avgRating, totalStudents);
    }

    /**
     * Lấy danh sách chi tiết tiến độ các bài học thuộc đợt đăng ký (STT 24).
     */
    public List<LessonProgressResponse> getEnrollmentProgress(Long enrollmentId, UserPrincipal currentUser) {
        log.debug("Lấy danh sách tiến độ bài học của Enrollment ID: {} cho User ID: {}", enrollmentId, currentUser.getId());

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));

        checkEnrollmentOwnership(enrollment, currentUser);

        List<LessonProgress> progressList = lessonProgressRepository.findByEnrollmentId(enrollmentId);
        return progressList.stream()
                .map(lessonProgressMapper::toResponse)
                .toList();
    }

    /**
     * Đánh dấu hoàn thành một bài học trong khóa học (STT 25).
     * Tự động tính toán lại % tiến độ và cập nhật trạng thái COMPLETED khi học hoàn thành 100%.
     */
    @Transactional
    public LessonProgressResponse completeLesson(Long enrollmentId, Long lessonId, UserPrincipal currentUser) {
        log.debug("User ID {} hoàn thành lesson {} trong enrollment {}", currentUser.getId(), lessonId, enrollmentId);

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));

        checkEnrollmentOwnership(enrollment, currentUser);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));

        if (!lesson.isPublished()) {
            throw new AppException(ErrorCode.LESSON_NOT_PUBLISHED);
        }

        if (!lesson.getCourse().getId().equals(enrollment.getCourse().getId())) {
            throw new AppException(ErrorCode.LESSON_NOT_IN_COURSE);
        }

        LessonProgress progress = lessonProgressRepository
                .findByEnrollmentIdAndLessonId(enrollmentId, lessonId)
                .orElseGet(() -> LessonProgress.builder()
                        .enrollment(enrollment)
                        .lesson(lesson)
                        .build());

        if (progress.isCompleted()) {
            throw new AppException(ErrorCode.LESSON_ALREADY_COMPLETED);
        }

        LocalDateTime now = LocalDateTime.now();
        progress.setCompleted(true);
        progress.setCompletedAt(now);
        progress.setLastAccessedAt(now);

        LessonProgress savedProgress = lessonProgressRepository.save(progress);

        // Cập nhật lại % tiến độ và trạng thái hoàn thành khóa học
        recalculateEnrollmentProgress(enrollment);

        log.info("Đánh dấu hoàn thành bài học thành công. Enrollment ID: {}, Lesson ID: {}", enrollmentId, lessonId);
        return lessonProgressMapper.toResponse(savedProgress);
    }

    /**
     * Helper Method: Tự động tính toán lại % tiến độ học tập dựa trên tổng số bài học đã xuất bản.
     */
    private void recalculateEnrollmentProgress(Enrollment enrollment) {
        long totalPublishedLessons = lessonRepository.countByCourseIdAndPublishedTrue(enrollment.getCourse().getId());
        long completedLessons = lessonProgressRepository.countByEnrollmentIdAndCompletedTrue(enrollment.getId());

        if (totalPublishedLessons > 0) {
            double percentage = ((double) completedLessons / totalPublishedLessons) * 100.0;
            double roundedPercentage = Math.min(100.0, Math.round(percentage * 100.0) / 100.0);
            enrollment.setProgressPercentage(roundedPercentage);

            if (roundedPercentage >= 100.0 && enrollment.getStatus() != EnrollmentStatus.COMPLETED) {
                enrollment.setStatus(EnrollmentStatus.COMPLETED);
                enrollment.setCompletionDate(LocalDateTime.now());
            }
        }
    }

    /**
     * Helper Method: Kiểm tra quyền truy cập thông tin đăng ký (Chính chủ học viên hoặc ADMIN).
     */
    private void checkEnrollmentOwnership(Enrollment enrollment, UserPrincipal currentUser) {
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwnerStudent = enrollment.getStudent().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwnerStudent) {
            log.warn("User ID {} không có quyền truy cập Enrollment ID {}", currentUser.getId(), enrollment.getId());
            throw new AppException(ErrorCode.FORBIDDEN_RESOURCE);
        }
    }
}