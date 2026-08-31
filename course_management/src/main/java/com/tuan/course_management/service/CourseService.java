package com.tuan.course_management.service;

import com.tuan.course_management.dto.request.CourseCreateRequest;
import com.tuan.course_management.dto.request.CourseUpdateRequest;
import com.tuan.course_management.dto.request.UpdateCourseStatusRequest;
import com.tuan.course_management.dto.response.CourseDetailResponse;
import com.tuan.course_management.dto.response.CourseResponse;
import com.tuan.course_management.dto.response.PageResponse;
import com.tuan.course_management.entity.Course;
import com.tuan.course_management.entity.Lesson;
import com.tuan.course_management.entity.User;
import com.tuan.course_management.enums.CourseStatus;
import com.tuan.course_management.enums.Role;
import com.tuan.course_management.exception.AppException;
import com.tuan.course_management.exception.ErrorCode;
import com.tuan.course_management.mapper.CourseMapper;
import com.tuan.course_management.repository.CourseRepository;
import com.tuan.course_management.repository.LessonRepository;
import com.tuan.course_management.repository.UserRepository;
import com.tuan.course_management.util.PageUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Dịch vụ xử lý nghiệp vụ liên quan đến khóa học.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final LessonRepository lessonRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "title", "status", "createdAt"
    );

    /**
     * Lấy danh sách khóa học có phân trang, tìm kiếm và lọc động.
     * Quy tắc phân quyền (STT 32): ADMIN thấy tất cả trạng thái, STUDENT/TEACHER chỉ thấy PUBLISHED.
     */
    public PageResponse<CourseResponse> getCourses(int page, int size, String sortBy, String sortDir,
                                                   String search, Long teacherId, CourseStatus status) {
        log.debug("Truy vấn danh sách khóa học - Page: {}, Size: {}, Search: {}, TeacherID: {}, Status: {}",
                page, size, search, teacherId, status);

        String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";
        Pageable pageable = PageUtils.createPageable(page, size, safeSortBy, sortDir, "createdAt");

        boolean isAdmin = isCurrentUserAdmin();

        Specification<Course> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. Phân quyền xem theo Trạng thái
            if (!isAdmin) {
                // Người dùng không phải ADMIN (STUDENT, TEACHER) chỉ được xem khóa học PUBLISHED
                predicates.add(cb.equal(root.get("status"), CourseStatus.PUBLISHED));
            } else if (status != null) {
                // ADMIN có quyền lọc theo bất kỳ trạng thái nào truyền lên
                predicates.add(cb.equal(root.get("status"), status));
            }

            // 2. Lọc theo từ khóa tìm kiếm
            if (search != null && !search.isBlank()) {
                String keyword = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), keyword),
                        cb.like(cb.lower(root.get("description")), keyword)
                ));
            }

            // 3. Lọc theo giảng viên phụ trách
            if (teacherId != null) {
                predicates.add(cb.equal(root.get("teacher").get("id"), teacherId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Course> coursePage = courseRepository.findAll(spec, pageable);
        return PageResponse.from(coursePage.map(CourseMapper::toResponse));
    }

    /**
     * Lấy chi tiết khóa học kèm danh sách bài học đã xuất bản.
     */
    public CourseDetailResponse getCourseById(Long courseId) {
        log.debug("Truy vấn chi tiết khóa học ID: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        List<Lesson> publishedLessons = lessonRepository.findByCourseIdAndPublishedTrue(courseId, Pageable.unpaged())
                .getContent();

        return CourseMapper.toDetailResponse(course, publishedLessons);
    }

    /**
     * Tạo mới khóa học (ADMIN). Trạng thái mặc định là DRAFT.
     */
    @Transactional
    public CourseResponse createCourse(CourseCreateRequest request) {
        log.debug("Bắt đầu tạo khóa học mới: title={}, teacherId={}", request.getTitle(), request.getTeacherId());

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .teacher(teacher)
                .status(CourseStatus.DRAFT)
                .build();

        Course savedCourse = courseRepository.save(course);
        log.info("Tạo khóa học thành công. CourseID: {}", savedCourse.getId());

        return CourseMapper.toResponse(savedCourse);
    }

    /**
     * Cập nhật thông tin khóa học (ADMIN). Tận dụng Dirty Checking.
     */
    @Transactional
    public CourseResponse updateCourse(Long courseId, CourseUpdateRequest request) {
        log.debug("Cập nhật thông tin khóa học ID: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (request.getTitle() != null) course.setTitle(request.getTitle());
        if (request.getDescription() != null) course.setDescription(request.getDescription());
        if (request.getTeacherId() != null && !request.getTeacherId().equals(course.getTeacher().getId())) {
            User teacher = userRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            course.setTeacher(teacher);
        }

        log.info("Cập nhật khóa học thành công. CourseID: {}", course.getId());
        return CourseMapper.toResponse(course);
    }

    /**
     * Cập nhật trạng thái khóa học (ADMIN). Tận dụng Dirty Checking.
     */
    @Transactional
    public CourseResponse updateCourseStatus(Long courseId, UpdateCourseStatusRequest request) {
        log.debug("Cập nhật trạng thái khóa học ID: {} sang {}", courseId, request.getStatus());

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        course.setStatus(request.getStatus());
        log.info("Cập nhật trạng thái thành công. CourseID: {}, Status: {}", course.getId(), course.getStatus());

        return CourseMapper.toResponse(course);
    }

    /**
     * Xóa khóa học khỏi hệ thống (ADMIN).
     */
    @Transactional
    public void deleteCourse(Long courseId) {
        log.debug("Xóa khóa học ID: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        courseRepository.delete(course);
        log.info("Xóa khóa học thành công. CourseID: {}", courseId);
    }

    /**
     * Helper Method kiểm tra xem người dùng hiện tại trong Security Context có vai trò ADMIN hay không.
     */
    private boolean isCurrentUserAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(Role.ADMIN.getAuthority())
                        || a.getAuthority().equals(Role.ADMIN.name()));
    }
}