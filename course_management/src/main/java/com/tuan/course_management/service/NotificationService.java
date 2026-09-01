package com.tuan.course_management.service;

import com.tuan.course_management.dto.request.NotificationCreateRequest;
import com.tuan.course_management.dto.response.NotificationResponse;
import com.tuan.course_management.dto.response.PageResponse;
import com.tuan.course_management.entity.Notification;
import com.tuan.course_management.entity.User;
import com.tuan.course_management.exception.AppException;
import com.tuan.course_management.exception.ErrorCode;
import com.tuan.course_management.mapper.NotificationMapper;
import com.tuan.course_management.repository.NotificationRepository;
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
 * Dịch vụ xử lý các nghiệp vụ quản lý thông báo.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;
    private final NotificationMapper notificationMapper;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "createdAt");

    /**
     * Lấy danh sách thông báo của người dùng hiện tại có phân trang (STT 33).
     */
    public PageResponse<NotificationResponse> getNotifications(UserPrincipal currentUser,
                                                               int page, int size, String sortBy, String sortDir) {
        Long userId = currentUser.getId();
        log.debug("Lấy danh sách thông báo cho User ID: {}", userId);

        String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(safeSortBy).ascending() : Sort.by(safeSortBy).descending();
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, sort);

        Page<Notification> notificationPage = notificationRepository.findByUserId(userId, pageable);
        List<NotificationResponse> mappedContent = notificationPage.getContent().stream()
                .map(notificationMapper::toResponse)
                .toList();

        return PageResponse.from(notificationPage, mappedContent);
    }

    /**
     * Đánh dấu thông báo là đã đọc (STT 34) - Tận dụng JPA Dirty Checking.
     */
    @Transactional
    public NotificationResponse markAsRead(Long notificationId, UserPrincipal currentUser) {
        Long userId = currentUser.getId();
        log.debug("User ID {} đánh dấu thông báo ID: {} đã đọc", userId, notificationId);

        Notification notification = getNotificationEntityById(notificationId);

        if (notification.getUser() == null || !notification.getUser().getId().equals(userId)) {
            log.warn("User ID {} không có quyền đánh dấu thông báo ID: {}", userId, notificationId);
            throw new AppException(ErrorCode.FORBIDDEN_RESOURCE);
        }

        if (!notification.isRead()) {
            notification.setRead(true);
            log.info("Thông báo ID: {} đã được đánh dấu đọc bởi User ID: {}", notificationId, userId);
        }

        return notificationMapper.toResponse(notification);
    }

    /**
     * Tạo thông báo mới cho người dùng (STT 35) - Chỉ ADMIN.
     */
    @Transactional
    public NotificationResponse createNotification(NotificationCreateRequest request) {
        log.debug("Tạo thông báo mới cho User ID: {}", request.getUserId());

        User recipient = userService.getUserEntityById(request.getUserId());
        Notification notification = notificationMapper.toEntity(request, recipient);

        Notification saved = notificationRepository.save(notification);
        log.info("Tạo thông báo thành công. Notification ID: {}", saved.getId());

        return notificationMapper.toResponse(saved);
    }

    /**
     * Xóa thông báo khỏi hệ thống (STT 36) - Chỉ ADMIN.
     */
    @Transactional
    public void deleteNotification(Long notificationId) {
        log.debug("Xóa thông báo ID: {}", notificationId);

        Notification notification = getNotificationEntityById(notificationId);
        notificationRepository.delete(notification);
        log.info("Xóa thông báo thành công. Notification ID: {}", notificationId);
    }

    // =========================================================================
    // HELPER METHODS
    // =========================================================================

    public Notification getNotificationEntityById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
    }
}