package com.tuan.course_management.service;

import com.tuan.course_management.dto.request.LessonCreateRequest;
import com.tuan.course_management.dto.request.LessonUpdateRequest;
import com.tuan.course_management.dto.response.LessonResponse;
import com.tuan.course_management.dto.response.LessonSummaryResponse;
import com.tuan.course_management.dto.response.PageResponse;
import com.tuan.course_management.entity.Course;
import com.tuan.course_management.entity.Lesson;
import com.tuan.course_management.enums.Role;
import com.tuan.course_management.exception.AppException;
import com.tuan.course_management.exception.ErrorCode;
import com.tuan.course_management.mapper.LessonMapper;
import com.tuan.course_management.repository.CourseRepository;
import com.tuan.course_management.repository.LessonRepository;
import com.tuan.course_management.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * Dịch vụ xử lý các nghiệp vụ quản lý bài học trong khóa học.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final LessonMapper lessonMapper;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "title", "orderIndex", "published", "createdAt"
    );

    /**
     * Lấy danh sách bài học đã xuất bản của một khóa học có phân trang (STT 16).
     */
    public PageResponse<LessonSummaryResponse> getPublishedLessons(Long courseId, int page, int size, String sortBy, String sortDir) {
        log.debug("Lấy danh sách bài học đã xuất bản cho Course ID: {}", courseId);

        if (!courseRepository.existsById(courseId)) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }

        String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "orderIndex";
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(safeSortBy).descending() : Sort.by(safeSortBy).ascending();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, sort);

        Page<Lesson> lessonPage = lessonRepository.findByCourseIdAndPublishedTrueOrderByOrderIndexAsc(courseId, pageable);
        List<LessonSummaryResponse> mappedContent = lessonPage.getContent().stream()
                .map(lessonMapper::toSummaryResponse)
                .toList();

        return PageResponse.from(lessonPage, mappedContent);
    }

    /**
     * Lấy chi tiết thông tin bài học (STT 17).
     * Phân quyền: Học viên chỉ xem được bài học đã xuất bản, Giảng viên sở hữu / ADMIN xem được cả bài học nháp.
     */
    public LessonResponse getLessonById(Long lessonId, UserPrincipal currentUser) {
        log.debug("Lấy chi tiết bài học ID: {} bởi User ID: {}", lessonId, currentUser.getId());

        Lesson lesson = getLessonEntityById(lessonId);

        if (!lesson.isPublished()) {
            checkCourseOwnership(lesson.getCourse(), currentUser);
        }

        return lessonMapper.toResponse(lesson);
    }

    /**
     * Thêm bài học mới vào khóa học (STT 18).
     * Yêu cầu: ADMIN hoặc TEACHER trực tiếp phụ trách khóa học.
     */
    @Transactional
    public LessonResponse createLesson(Long courseId, LessonCreateRequest request, UserPrincipal currentUser) {
        log.debug("Thêm bài học mới vào Course ID: {} bởi User ID: {}", courseId, currentUser.getId());

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        checkCourseOwnership(course, currentUser);

        Lesson lesson = lessonMapper.toEntity(request, course);
        Lesson savedLesson = lessonRepository.save(lesson);

        log.info("Tạo bài học thành công. Lesson ID: {}", savedLesson.getId());
        return lessonMapper.toResponse(savedLesson);
    }

    /**
     * Cập nhật thông tin bài học (STT 19). Tận dụng JPA Dirty Checking.
     */
    @Transactional
    public LessonResponse updateLesson(Long lessonId, LessonUpdateRequest request, UserPrincipal currentUser) {
        log.debug("Cập nhật bài học ID: {} bởi User ID: {}", lessonId, currentUser.getId());

        Lesson lesson = getLessonEntityById(lessonId);
        checkCourseOwnership(lesson.getCourse(), currentUser);

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            lesson.setTitle(request.getTitle().trim());
        }
        if (request.getContentUrl() != null) {
            lesson.setContentUrl(request.getContentUrl().trim());
        }
        if (request.getTextContent() != null) {
            lesson.setTextContent(request.getTextContent().trim());
        }
        if (request.getOrderIndex() != null) {
            lesson.setOrderIndex(request.getOrderIndex());
        }
        if (request.getPublished() != null) {
            lesson.setPublished(request.getPublished());
        }

        log.info("Cập nhật bài học thành công. Lesson ID: {}", lesson.getId());
        return lessonMapper.toResponse(lesson);
    }

    /**
     * Xuất bản (Publish) bài học (STT 20).
     */
    @Transactional
    public LessonResponse publishLesson(Long lessonId, UserPrincipal currentUser) {
        log.debug("Publish bài học ID: {} bởi User ID: {}", lessonId, currentUser.getId());

        Lesson lesson = getLessonEntityById(lessonId);
        checkCourseOwnership(lesson.getCourse(), currentUser);

        if (lesson.isPublished()) {
            throw new AppException(ErrorCode.LESSON_ALREADY_PUBLISHED);
        }

        lesson.setPublished(true);
        log.info("Bài học đã được xuất bản thành công. Lesson ID: {}", lesson.getId());

        return lessonMapper.toResponse(lesson);
    }

    /**
     * Xóa bài học khỏi hệ thống (STT 21).
     */
    @Transactional
    public void deleteLesson(Long lessonId, UserPrincipal currentUser) {
        log.debug("Xóa bài học ID: {} bởi User ID: {}", lessonId, currentUser.getId());

        Lesson lesson = getLessonEntityById(lessonId);
        checkCourseOwnership(lesson.getCourse(), currentUser);

        lessonRepository.delete(lesson);
        log.info("Xóa bài học thành công. Lesson ID: {}", lessonId);
    }

    /**
     * Xem trước nội dung rút gọn của bài học (STT 44).
     */
    public LessonResponse getContentPreview(Long lessonId) {
        log.debug("Xem trước nội dung bài học ID: {}", lessonId);

        Lesson lesson = getLessonEntityById(lessonId);

        if (!lesson.isPublished()) {
            throw new AppException(ErrorCode.LESSON_NOT_PUBLISHED);
        }

        String rawContent = lesson.getTextContent();
        String preview = (rawContent != null && rawContent.length() > 100)
                ? rawContent.substring(0, 100) + "..."
                : rawContent;

        LessonResponse response = lessonMapper.toResponse(lesson);
        response.setTextContent(preview);

        return response;
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    public Lesson getLessonEntityById(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
    }

    private void checkCourseOwnership(Course course, UserPrincipal currentUser) {
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwnerTeacher = course.getTeacher() != null && course.getTeacher().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwnerTeacher) {
            log.warn("User ID {} không có quyền thao tác trên Course ID {}", currentUser.getId(), course.getId());
            throw new AppException(ErrorCode.LESSON_ACCESS_DENIED);
        }
    }
}