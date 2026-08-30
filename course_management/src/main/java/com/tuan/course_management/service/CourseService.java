package com.tuan.course_management.service;

import com.tuan.course_management.dto.request.CourseRequest;
import com.tuan.course_management.dto.request.UpdateCourseStatusRequest;
import com.tuan.course_management.dto.response.CourseDetailResponse;
import com.tuan.course_management.dto.response.CourseResponse;
import com.tuan.course_management.dto.response.PageResponse;
import com.tuan.course_management.entity.Course;
import com.tuan.course_management.entity.Lesson;
import com.tuan.course_management.entity.User;
import com.tuan.course_management.enums.CourseStatus;
import com.tuan.course_management.exception.AppException;
import com.tuan.course_management.exception.ErrorCode;
import com.tuan.course_management.mapper.CourseMapper;
import com.tuan.course_management.repository.CourseRepository;
import com.tuan.course_management.repository.LessonRepository;
import com.tuan.course_management.repository.UserRepository;
import com.tuan.course_management.util.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    /**
     * Lấy danh sách khóa học có phân trang, tìm kiếm và lọc.
     */
    public PageResponse<CourseResponse> getCourses(int page, int size, String sortBy, String sortDir,
                                                   String search, Long teacherId, CourseStatus status) {
        log.debug("Truy vấn danh sách khóa học - Page: {}, Size: {}, Search: {}, TeacherID: {}, Status: {}",
                page, size, search, teacherId, status);

        Pageable pageable = PageUtils.createPageable(page, size, sortBy, sortDir, "createdAt");

        Specification<Course> spec = Specification.where(null);

        if (search != null && !search.isBlank()) {
            String keyword = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), keyword),
                    cb.like(cb.lower(root.get("description")), keyword)
            ));
        }
        if (teacherId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("teacher").get("id"), teacherId));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        Page<Course> coursePage = courseRepository.findAll(spec, pageable);
        Page<CourseResponse> responsePage = coursePage.map(CourseMapper::toResponse);

        return PageResponse.from(responsePage);
    }

    /**
     * Lấy chi tiết khóa học kèm danh sách bài học đã publish.
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
     * Tạo mới khóa học (ADMIN).
     */
    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        log.debug("Bắt đầu tạo khóa học mới: title={}, teacherId={}", request.getTitle(), request.getTeacherId());

        User teacher = userRepository.findById(request.getTeacherId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .teacher(teacher)
                .status(CourseStatus.DRAFT) // Mặc định DRAFT khi tạo
                .build();

        Course savedCourse = courseRepository.save(course);
        log.info("Tạo khóa học thành công. CourseID: {}", savedCourse.getId());

        return CourseMapper.toResponse(savedCourse);
    }

    /**
     * Cập nhật thông tin khóa học (ADMIN).
     */
    @Transactional
    public CourseResponse updateCourse(Long courseId, CourseRequest request) {
        log.debug("Cập nhật khóa học ID: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (request.getTitle() != null) course.setTitle(request.getTitle());
        if (request.getDescription() != null) course.setDescription(request.getDescription());
        if (request.getTeacherId() != null) {
            User teacher = userRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            course.setTeacher(teacher);
        }

        Course updatedCourse = courseRepository.save(course);
        log.info("Cập nhật khóa học thành công. CourseID: {}", updatedCourse.getId());

        return CourseMapper.toResponse(updatedCourse);
    }

    /**
     * Cập nhật trạng thái khóa học (ADMIN).
     */
    @Transactional
    public CourseResponse updateCourseStatus(Long courseId, UpdateCourseStatusRequest request) {
        log.debug("Cập nhật trạng thái khóa học ID: {} sang {}", courseId, request.getStatus());

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        course.setStatus(request.getStatus());
        Course updatedCourse = courseRepository.save(course);
        log.info("Cập nhật trạng thái khóa học thành công. CourseID: {}, Status: {}", updatedCourse.getId(), updatedCourse.getStatus());

        return CourseMapper.toResponse(updatedCourse);
    }

    /**
     * Xóa khóa học (ADMIN).
     */
    @Transactional
    public void deleteCourse(Long courseId) {
        log.debug("Xóa khóa học ID: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        courseRepository.delete(course);
        log.info("Xóa khóa học thành công. CourseID: {}", courseId);
    }
}