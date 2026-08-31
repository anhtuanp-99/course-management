package com.tuan.course_management.service;

import com.tuan.course_management.dto.request.EnrollmentRequest;
import com.tuan.course_management.dto.response.EnrollmentDetailResponse;
import com.tuan.course_management.dto.response.EnrollmentResponse;
import com.tuan.course_management.dto.response.PageResponse;
import com.tuan.course_management.entity.*;
import com.tuan.course_management.enums.CourseStatus;
import com.tuan.course_management.enums.Role;
import com.tuan.course_management.exception.AppException;
import com.tuan.course_management.exception.ErrorCode;
import com.tuan.course_management.mapper.EnrollmentMapper;
import com.tuan.course_management.repository.CourseRepository;
import com.tuan.course_management.repository.EnrollmentRepository;
import com.tuan.course_management.repository.LessonProgressRepository;
import com.tuan.course_management.repository.LessonRepository;
import com.tuan.course_management.security.UserPrincipal;
import com.tuan.course_management.util.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final UserService userService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "enrolledAt"
    );

    /**
     * Lấy danh sách các khóa học sinh viên đã đăng ký (Đáp ứng STT 22).
     */
    public PageResponse<EnrollmentResponse> getEnrollments(UserPrincipal currentUser,
                                                           int page, int size, String sortBy, String sortDir) {
        Long studentId = currentUser.getId();
        log.debug("Lấy danh sách đăng ký cho Student ID: {}", studentId);

        String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "enrolledAt";
        Pageable pageable = PageUtils.createPageable(page, size, safeSortBy, sortDir, "enrolledAt");

        Page<Enrollment> enrollmentPage = enrollmentRepository.findByStudentId(studentId, pageable);
        return PageResponse.from(enrollmentPage.map(EnrollmentMapper::toResponse));
    }

    /**
     * Đăng ký một khóa học mới (Đáp ứng STT 23).
     */
    @Transactional
    public EnrollmentResponse enroll(EnrollmentRequest request, UserPrincipal currentUser) {
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

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .build();

        Enrollment saved = enrollmentRepository.save(enrollment);
        log.info("Đăng ký khóa học thành công. Enrollment ID: {}", saved.getId());

        return EnrollmentMapper.toResponse(saved);
    }

    /**
     * Lấy chi tiết thông tin đăng ký kèm tiến độ học tập (Đáp ứng STT 24).
     */
    public EnrollmentDetailResponse getEnrollmentDetail(Long enrollmentId, UserPrincipal currentUser) {
        log.debug("Lấy chi tiết đăng ký ID: {} cho User ID: {}", enrollmentId, currentUser.getId());

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new AppException(ErrorCode.ENROLLMENT_NOT_FOUND));

        checkEnrollmentOwnership(enrollment, currentUser);

        List<LessonProgress> progressList = lessonProgressRepository.findByEnrollmentId(enrollmentId);

        return EnrollmentMapper.toDetailResponse(enrollment, progressList);
    }

    /**
     * Đánh dấu hoàn thành một bài học trong khóa học đã đăng ký (Đáp ứng STT 25).
     */
    @Transactional
    public void completeLesson(Long enrollmentId, Long lessonId, UserPrincipal currentUser) {
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

        // Tìm bản ghi tiến độ đã khởi tạo sẵn hoặc tạo đối tượng mới nếu chưa có
        LessonProgress progress = lessonProgressRepository
                .findByEnrollmentIdAndLessonId(enrollmentId, lessonId)
                .orElseGet(() -> LessonProgress.builder()
                        .enrollment(enrollment)
                        .lesson(lesson)
                        .build());

        // Kiểm tra chính xác cờ completed của bản ghi tiến độ
        if (progress.isCompleted()) {
            throw new AppException(ErrorCode.LESSON_ALREADY_COMPLETED);
        }

        progress.setCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());

        // Nếu là bản ghi khởi tạo mới thì lưu vào DB, bản ghi cũ tận dụng Dirty Checking
        if (progress.getId() == null) {
            lessonProgressRepository.save(progress);
        }

        log.info("Đánh dấu hoàn thành bài học thành công. Enrollment ID: {}, Lesson ID: {}", enrollmentId, lessonId);
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    /**
     * Kiểm tra quyền truy cập thông tin đăng ký: Chỉ chính học viên sở hữu hoặc ADMIN.
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