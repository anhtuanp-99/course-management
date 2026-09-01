package com.tuan.course_management.service;

import com.tuan.course_management.dto.request.CourseCreateRequest;
import com.tuan.course_management.dto.request.CourseUpdateRequest;
import com.tuan.course_management.dto.response.CourseDetailResponse;
import com.tuan.course_management.dto.response.CourseSummaryResponse;
import com.tuan.course_management.dto.response.PageResponse;
import com.tuan.course_management.entity.Course;
import com.tuan.course_management.entity.User;
import com.tuan.course_management.enums.CourseStatus;
import com.tuan.course_management.enums.Role;
import com.tuan.course_management.exception.AppException;
import com.tuan.course_management.exception.ErrorCode;
import com.tuan.course_management.mapper.CourseMapper;
import com.tuan.course_management.repository.CourseRepository;
import com.tuan.course_management.repository.EnrollmentRepository;
import com.tuan.course_management.repository.LessonRepository;
import com.tuan.course_management.repository.ReviewRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Dịch vụ xử lý các nghiệp vụ quản lý khóa học.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ReviewRepository reviewRepository;
    private final UserService userService;
    private final CourseMapper courseMapper;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "title", "price", "durationHours", "status", "createdAt"
    );

    /**
     * Lấy danh sách khóa học có phân trang, tìm kiếm và lọc động.
     * Quy tắc phân quyền: ADMIN thấy tất cả trạng thái, STUDENT/TEACHER chỉ thấy PUBLISHED.
     */
    public PageResponse<CourseSummaryResponse> getCourses(int page, int size, String sortBy, String sortDir,
                                                          String search, Long teacherId, CourseStatus status) {
        log.debug("Truy vấn danh sách khóa học - Page: {}, Size: {}, Search: {}, TeacherID: {}, Status: {}",
                page, size, search, teacherId, status);

        String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(safeSortBy).ascending() : Sort.by(safeSortBy).descending();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, sort);

        boolean isAdmin = isCurrentUserAdmin();

        Specification<Course> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (!isAdmin) {
                predicates.add(cb.equal(root.get("status"), CourseStatus.PUBLISHED));
            } else if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (StringUtils.hasText(search)) {
                String keyword = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), keyword),
                        cb.like(cb.lower(root.get("description")), keyword)
                ));
            }

            if (teacherId != null) {
                predicates.add(cb.equal(root.get("teacher").get("id"), teacherId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Course> coursePage = courseRepository.findAll(spec, pageable);
        List<CourseSummaryResponse> mappedContent = coursePage.getContent().stream()
                .map(course -> {
                    Double avgRating = reviewRepository.calculateAvgRatingByCourseId(course.getId());
                    long totalStudents = enrollmentRepository.countByCourseId(course.getId());
                    return courseMapper.toSummaryResponse(course, avgRating, totalStudents);
                })
                .toList();

        return PageResponse.from(coursePage, mappedContent);
    }

    /**
     * Lấy chi tiết thông tin khóa học.
     * Bảo mật: Sinh viên/Giảng viên chỉ xem được khóa học trạng thái PUBLISHED.
     */
    public CourseDetailResponse getCourseById(Long courseId) {
        log.debug("Truy vấn chi tiết khóa học ID: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (!isCurrentUserAdmin() && course.getStatus() != CourseStatus.PUBLISHED) {
            log.warn("Người dùng không phải ADMIN cố gắng xem khóa học chưa xuất bản ID: {}", courseId);
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }

        Double avgRating = reviewRepository.calculateAvgRatingByCourseId(courseId);
        long totalStudents = enrollmentRepository.countByCourseId(courseId);
        long totalLessons = isCurrentUserAdmin()
                ? lessonRepository.countByCourseId(courseId)
                : lessonRepository.countByCourseIdAndPublishedTrue(courseId);

        return courseMapper.toDetailResponse(course, avgRating, totalStudents, totalLessons);
    }

    /**
     * Tạo mới khóa học (ADMIN). Trạng thái mặc định là DRAFT.
     */
    @Transactional
    public CourseSummaryResponse createCourse(CourseCreateRequest request) {
        log.debug("Bắt đầu tạo khóa học mới: title={}, teacherId={}", request.getTitle(), request.getTeacherId());

        User teacher = userService.getTeacherEntityById(request.getTeacherId());
        Course course = courseMapper.toEntity(request, teacher);

        Course savedCourse = courseRepository.save(course);
        log.info("Tạo khóa học thành công. CourseID: {}", savedCourse.getId());

        return courseMapper.toSummaryResponse(savedCourse, 0.0, 0L);
    }

    /**
     * Cập nhật thông tin khóa học (ADMIN). Tận dụng JPA Dirty Checking.
     */
    @Transactional
    public CourseSummaryResponse updateCourse(Long courseId, CourseUpdateRequest request) {
        log.debug("Cập nhật thông tin khóa học ID: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            course.setTitle(request.getTitle().trim());
        }
        if (request.getDescription() != null) {
            course.setDescription(request.getDescription().trim());
        }
        if (request.getPrice() != null) {
            course.setPrice(request.getPrice());
        }
        if (request.getDurationHours() != null) {
            course.setDurationHours(request.getDurationHours());
        }
        if (request.getStatus() != null) {
            course.setStatus(request.getStatus());
        }
        if (request.getTeacherId() != null && !request.getTeacherId().equals(course.getTeacher().getId())) {
            User teacher = userService.getTeacherEntityById(request.getTeacherId());
            course.setTeacher(teacher);
        }

        Double avgRating = reviewRepository.calculateAvgRatingByCourseId(courseId);
        long totalStudents = enrollmentRepository.countByCourseId(courseId);

        log.info("Cập nhật khóa học thành công. CourseID: {}", course.getId());
        return courseMapper.toSummaryResponse(course, avgRating, totalStudents);
    }

    /**
     * Cập nhật trạng thái hiển thị của khóa học (ADMIN).
     */
    @Transactional
    public CourseSummaryResponse updateCourseStatus(Long courseId, CourseStatus status) {
        log.debug("Cập nhật trạng thái khóa học ID: {} sang {}", courseId, status);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        course.setStatus(status);

        Double avgRating = reviewRepository.calculateAvgRatingByCourseId(courseId);
        long totalStudents = enrollmentRepository.countByCourseId(courseId);

        log.info("Cập nhật trạng thái thành công. CourseID: {}, Status: {}", course.getId(), course.getStatus());
        return courseMapper.toSummaryResponse(course, avgRating, totalStudents);
    }

    /**
     * Xóa khóa học khỏi hệ thống (ADMIN).
     * Ràng buộc: Không được xóa khóa học đã có học viên đăng ký.
     */
    @Transactional
    public void deleteCourse(Long courseId) {
        log.debug("Xóa khóa học ID: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        long totalStudents = enrollmentRepository.countByCourseId(courseId);
        if (totalStudents > 0) {
            log.warn("Không thể xóa khóa học ID: {} vì đã có {} học viên đăng ký", courseId, totalStudents);
            throw new AppException(ErrorCode.COURSE_HAS_ENROLLMENTS);
        }

        courseRepository.delete(course);
        log.info("Xóa khóa học thành công. CourseID: {}", courseId);
    }

    /**
     * Helper method kiểm tra người dùng hiện tại có vai trò ADMIN không.
     */
    private boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ADMIN.getAuthority())
                        || a.getAuthority().equals("ROLE_" + Role.ADMIN.name()));
    }
}