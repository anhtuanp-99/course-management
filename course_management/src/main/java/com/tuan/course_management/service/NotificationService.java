package com.tuan.course_management.service;

import com.tuan.course_management.dto.request.NotificationRequest;
import com.tuan.course_management.dto.response.NotificationResponse;
import com.tuan.course_management.dto.response.PageResponse;
import com.tuan.course_management.entity.Notification;
import com.tuan.course_management.entity.User;
import com.tuan.course_management.exception.AppException;
import com.tuan.course_management.exception.ErrorCode;
import com.tuan.course_management.mapper.NotificationMapper;
import com.tuan.course_management.repository.NotificationRepository;
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
 * Dịch vụ xử lý nghiệp vụ liên quan đến thông báo.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of("id", "createdAt");

    /**
     * Lấy danh sách thông báo của người dùng hiện tại (Đáp ứng STT 33).
     */
    public PageResponse<NotificationResponse> getNotifications(UserPrincipal currentUser,
                                                               int page, int size, String sortBy, String sortDir) {
        Long userId = currentUser.getId();
        log.debug("Lấy danh sách thông báo cho User ID: {}", userId);

        String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "createdAt";
        Pageable pageable = PageUtils.createPageable(page, size, safeSortBy, sortDir, "createdAt");

        Page<Notification> notificationPage = notificationRepository.findByUserId(userId, pageable);
        return PageResponse.from(notificationPage.map(NotificationMapper::toResponse));
    }

    /**
     * Đánh dấu thông báo đã đọc (Đáp ứng STT 34).
     * Tận dụng JPA Dirty Checking để tự động cập nhật Database.
     */
    @Transactional
    public void markAsRead(Long notificationId, UserPrincipal currentUser) {
        Long userId = currentUser.getId();
        log.debug("User ID {} đánh dấu thông báo ID: {} đã đọc", userId, notificationId);

        Notification notification = getNotificationEntityById(notificationId);

        if (notification.getUser() == null || !notification.getUser().getId().equals(userId)) {
            log.warn("User ID {} không có quyền đánh dấu thông báo ID: {}", userId, notificationId);
            throw new AppException(ErrorCode.NOTIFICATION_ACCESS_DENIED);
        }

        if (!notification.isRead()) {
            notification.setRead(true);
            log.info("Thông báo ID: {} đã được đánh dấu đọc bởi User ID: {}", notificationId, userId);
        }
    }

    /**
     * Tạo thông báo mới cho người dùng (Đáp ứng STT 35) - Chỉ ADMIN.
     */
    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        log.debug("Tạo thông báo mới cho User ID: {}", request.getUserId());

        User recipient = userService.getUserEntityById(request.getUserId());

        Notification notification = Notification.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .user(recipient)
                .read(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        log.info("Tạo thông báo thành công. Notification ID: {}", saved.getId());

        return NotificationMapper.toResponse(saved);
    }

    /**
     * Xóa thông báo khỏi hệ thống (Đáp ứng STT 36) - Chỉ ADMIN.
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

    /**
     * Truy vấn Entity Notification theo ID, ném NOTIFICATION_NOT_FOUND nếu không tồn tại.
     */
    public Notification getNotificationEntityById(Long notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new AppException(ErrorCode.NOTIFICATION_NOT_FOUND));
    }
}