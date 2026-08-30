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
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    // Danh sách các trường được phép sắp xếp (Whitelist chống PropertyReferenceException)
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "title", "status", "createdAt"
    );

    /**
     * Lấy danh sách khóa học có phân trang, tìm kiếm và lọc động.
     *
     * @param page Số trang truy vấn
     * @param size Số lượng bản ghi mỗi trang
     * @param sortBy Cột thực hiện sắp xếp
     * @param sortDir Hướng sắp xếp (ASC/DESC)
     * @param search Từ khóa tìm kiếm (tiêu đề/mô tả)
     * @param teacherId Lọc theo giảng viên
     * @param status Lọc theo trạng thái khóa học
     * @return PageResponse Danh sách khóa học dạng DTO phân trang
     */
    public PageResponse<CourseResponse> getCourses(int page, int size, String sortBy, String sortDir,
                                                   String search, Long teacherId, CourseStatus status) {
        log.debug("Truy vấn danh sách khóa học - Page: {}, Size: {}, Search: {}, TeacherID: {}, Status: {}",
                page, size, search, teacherId, status);

        // Sanitize trường sắp xếp đầu vào
        String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";
        Pageable pageable = PageUtils.createPageable(page, size, safeSortBy, sortDir, "createdAt");

        Specification<Course> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (search != null && !search.isBlank()) {
                String keyword = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), keyword),
                        cb.like(cb.lower(root.get("description")), keyword)
                ));
            }
            if (teacherId != null) {
                predicates.add(cb.equal(root.get("teacher").get("id"), teacherId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Course> coursePage = courseRepository.findAll(spec, pageable);
        return PageResponse.from(coursePage.map(CourseMapper::toResponse));
    }

    /**
     * Lấy chi tiết khóa học kèm danh sách bài học đã xuất bản (Published).
     *
     * @param courseId Mã định danh khóa học
     * @return CourseDetailResponse Chi tiết khóa học kèm bài học
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
     * Tạo mới khóa học với trạng thái mặc định DRAFT.
     *
     * @param request Thông tin tạo khóa học
     * @return CourseResponse Thông tin khóa học sau khi khởi tạo
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
                .status(CourseStatus.DRAFT)
                .build();

        Course savedCourse = courseRepository.save(course);
        log.info("Tạo khóa học thành công cho Course ID: {}", savedCourse.getId());

        return CourseMapper.toResponse(savedCourse);
    }

    /**
     * Cập nhật thông tin khóa học. Tận dụng JPA Dirty Checking.
     *
     * @param courseId Mã định danh khóa học
     * @param request Thông tin cập nhật
     * @return CourseResponse Thông tin khóa học sau khi cập nhật
     */
    @Transactional
    public CourseResponse updateCourse(Long courseId, CourseRequest request) {
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

        log.info("Cập nhật khóa học thành công cho Course ID: {}", course.getId());
        return CourseMapper.toResponse(course);
    }

    /**
     * Cập nhật trạng thái hiển thị của khóa học. Tận dụng JPA Dirty Checking.
     *
     * @param courseId Mã định danh khóa học
     * @param request Trạng thái mới
     * @return CourseResponse Thông tin khóa học sau khi đổi trạng thái
     */
    @Transactional
    public CourseResponse updateCourseStatus(Long courseId, UpdateCourseStatusRequest request) {
        log.debug("Cập nhật trạng thái khóa học ID: {} sang {}", courseId, request.getStatus());

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        course.setStatus(request.getStatus());
        log.info("Cập nhật trạng thái thành công cho Course ID: {}, Status: {}", course.getId(), course.getStatus());

        return CourseMapper.toResponse(course);
    }

    /**
     * Xóa khóa học khỏi hệ thống.
     *
     * @param courseId Mã định danh khóa học
     */
    @Transactional
    public void deleteCourse(Long courseId) {
        log.debug("Thực hiện xóa khóa học cho Course ID: {}", courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        courseRepository.delete(course);
        log.info("Xóa khóa học thành công cho Course ID: {}", courseId);
    }
}