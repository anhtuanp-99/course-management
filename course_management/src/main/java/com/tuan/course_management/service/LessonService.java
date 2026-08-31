package com.tuan.course_management.service;

import com.tuan.course_management.dto.request.LessonRequest;
import com.tuan.course_management.dto.response.LessonResponse;
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
import com.tuan.course_management.util.PageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Dịch vụ xử lý nghiệp vụ liên quan đến bài học.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "title", "published", "createdAt"
    );

    /**
     * Lấy danh sách bài học đã xuất bản của một khóa học (Đáp ứng STT 16).
     */
    public PageResponse<LessonResponse> getPublishedLessons(Long courseId, int page, int size, String sortBy, String sortDir) {
        log.debug("Lấy danh sách bài học đã xuất bản cho Course ID: {}", courseId);

        if (!courseRepository.existsById(courseId)) {
            throw new AppException(ErrorCode.COURSE_NOT_FOUND);
        }

        String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";
        Pageable pageable = PageUtils.createPageable(page, size, safeSortBy, sortDir, "createdAt");
        Page<Lesson> lessonPage = lessonRepository.findByCourseIdAndPublishedTrue(courseId, pageable);

        return PageResponse.from(lessonPage.map(LessonMapper::toResponse));
    }

    /**
     * Lấy chi tiết thông tin một bài học đã xuất bản (Đáp ứng STT 17).
     */
    public LessonResponse getLessonById(Long lessonId) {
        log.debug("Lấy chi tiết bài học ID: {}", lessonId);

        Lesson lesson = getLessonEntityById(lessonId);

        if (!lesson.isPublished()) {
            throw new AppException(ErrorCode.LESSON_NOT_PUBLISHED);
        }

        return LessonMapper.toResponse(lesson);
    }

    /**
     * Thêm bài học mới vào khóa học (Đáp ứng STT 18).
     * Yêu cầu: ADMIN hoặc TEACHER phụ trách khóa học.
     */
    @Transactional
    public LessonResponse createLesson(Long courseId, LessonRequest request, UserPrincipal currentUser) {
        log.debug("Thêm bài học mới vào Course ID: {} bởi User ID: {}", courseId, currentUser.getId());

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        checkCourseOwnership(course, currentUser);

        Lesson lesson = Lesson.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .course(course)
                .published(false)
                .build();

        Lesson savedLesson = lessonRepository.save(lesson);
        log.info("Tạo bài học thành công. Lesson ID: {}", savedLesson.getId());

        return LessonMapper.toResponse(savedLesson);
    }

    /**
     * Cập nhật bài học (Đáp ứng STT 19).
     */
    @Transactional
    public LessonResponse updateLesson(Long lessonId, LessonRequest request, UserPrincipal currentUser) {
        log.debug("Cập nhật bài học ID: {} bởi User ID: {}", lessonId, currentUser.getId());

        Lesson lesson = getLessonEntityById(lessonId);
        checkCourseOwnership(lesson.getCourse(), currentUser);

        if (request.getTitle() != null) lesson.setTitle(request.getTitle());
        if (request.getContent() != null) lesson.setContent(request.getContent());

        log.info("Cập nhật bài học thành công. Lesson ID: {}", lesson.getId());
        return LessonMapper.toResponse(lesson);
    }

    /**
     * Xuất bản (Publish) bài học (Đáp ứng STT 20).
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
        log.info("Bài học đã được publish. Lesson ID: {}", lesson.getId());
        return LessonMapper.toResponse(lesson);
    }

    /**
     * Xóa bài học khỏi hệ thống (Đáp ứng STT 21).
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
     * Xem trước nội dung rút gọn của bài học (Đáp ứng STT 44).
     */
    public LessonResponse getContentPreview(Long lessonId) {
        log.debug("Xem trước nội dung bài học ID: {}", lessonId);

        Lesson lesson = getLessonEntityById(lessonId);

        if (!lesson.isPublished()) {
            throw new AppException(ErrorCode.LESSON_NOT_PUBLISHED);
        }

        String rawContent = lesson.getContent();
        String preview = (rawContent != null)
                ? rawContent.substring(0, Math.min(rawContent.length(), 100)) + "..."
                : "";

        LessonResponse response = LessonMapper.toResponse(lesson);
        response.setContent(preview);
        return response;
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    /**
     * Truy vấn Entity Lesson theo ID, ném Exception nếu không tìm thấy.
     */
    public Lesson getLessonEntityById(Long lessonId) {
        return lessonRepository.findById(lessonId)
                .orElseThrow(() -> new AppException(ErrorCode.LESSON_NOT_FOUND));
    }

    /**
     * Kiểm tra quyền sở hữu khóa học: Chỉ ADMIN hoặc TEACHER trực tiếp phụ trách khóa học.
     */
    private void checkCourseOwnership(Course course, UserPrincipal currentUser) {
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;
        boolean isOwnerTeacher = course.getTeacher() != null && course.getTeacher().getId().equals(currentUser.getId());

        if (!isAdmin && !isOwnerTeacher) {
            log.warn("User ID {} không có quyền thao tác trên Course ID {}", currentUser.getId(), course.getId());
            throw new AppException(ErrorCode.LESSON_ACCESS_DENIED);
        }
    }
}