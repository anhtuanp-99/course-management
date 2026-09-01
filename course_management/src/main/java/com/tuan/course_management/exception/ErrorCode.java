package com.tuan.course_management.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * Enum quản lý tập trung toàn bộ mã lỗi, String constant, thông báo và HTTP Status.
 * Khớp hoàn toàn với cấu trúc Error Response Wrapper Envelope trong SRS Section 6.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ================= AUTH & USER =================
    USER_NOT_FOUND(1001, "RESOURCE_NOT_FOUND", "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    USERNAME_ALREADY_EXISTS(1002, "DUPLICATE_RESOURCE", "Tên tài khoản đã được sử dụng", HttpStatus.CONFLICT),
    EMAIL_ALREADY_EXISTS(1003, "DUPLICATE_RESOURCE", "Email đã được sử dụng trong hệ thống", HttpStatus.CONFLICT),
    INVALID_PASSWORD(1004, "BAD_CREDENTIALS", "Mật khẩu hiện tại không chính xác", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_ACCESS(1005, "INVALID_JWT_TOKEN", "Bạn cần đăng nhập để truy cập tài nguyên này", HttpStatus.UNAUTHORIZED),
    FORBIDDEN_RESOURCE(1006, "ACCESS_DENIED", "Bạn không có quyền thực hiện thao tác này", HttpStatus.FORBIDDEN),
    CANNOT_CHANGE_OWN_ROLE(1007, "INVALID_INPUT_DATA", "Bạn không thể thay đổi vai trò của chính mình", HttpStatus.BAD_REQUEST),
    ACCOUNT_DISABLED(1008, "ACCESS_DENIED", "Tài khoản của bạn đã bị khóa hoặc chưa kích hoạt", HttpStatus.FORBIDDEN),
    EXPIRED_JWT_TOKEN(1009, "EXPIRED_JWT_TOKEN", "Token đã hết hạn sử dụng", HttpStatus.UNAUTHORIZED),
    INVALID_JWT_TOKEN(1010, "INVALID_JWT_TOKEN", "Token không hợp lệ hoặc bị thay đổi", HttpStatus.UNAUTHORIZED),
    CANNOT_UPDATE_ADMIN_ROLE(1011, "ACCESS_DENIED", "Không được phép thay đổi vai trò của Quản trị viên khác", HttpStatus.FORBIDDEN),
    USER_NOT_TEACHER(1012, "INVALID_INPUT_DATA", "Người dùng được gán không phải là Giảng viên", HttpStatus.BAD_REQUEST),

    // ================= COURSE =================
    COURSE_NOT_FOUND(2001, "RESOURCE_NOT_FOUND", "Không tìm thấy khóa học", HttpStatus.NOT_FOUND),
    COURSE_ALREADY_PUBLISHED(2002, "INVALID_STATE_TRANSITION", "Khóa học đã được xuất bản", HttpStatus.BAD_REQUEST),
    COURSE_NOT_PUBLISHED(2003, "INVALID_STATE_TRANSITION", "Khóa học chưa được xuất bản", HttpStatus.BAD_REQUEST),
    COURSE_HAS_ENROLLMENTS(2004, "INVALID_STATE_TRANSITION", "Không thể xóa khóa học đã có học viên đăng ký", HttpStatus.CONFLICT),
    INVALID_COURSE_STATUS(2005, "INVALID_INPUT_DATA", "Trạng thái khóa học không hợp lệ", HttpStatus.BAD_REQUEST),

    // ================= LESSON =================
    LESSON_NOT_FOUND(3001, "RESOURCE_NOT_FOUND", "Không tìm thấy bài học", HttpStatus.NOT_FOUND),
    LESSON_ALREADY_PUBLISHED(3002, "INVALID_STATE_TRANSITION", "Bài học đã được xuất bản", HttpStatus.BAD_REQUEST),
    LESSON_NOT_PUBLISHED(3003, "INVALID_STATE_TRANSITION", "Bài học chưa được xuất bản", HttpStatus.BAD_REQUEST),
    LESSON_ACCESS_DENIED(3004, "ACCESS_DENIED", "Bạn không có quyền quản lý bài học này", HttpStatus.FORBIDDEN),

    // ================= ENROLLMENT =================
    ENROLLMENT_NOT_FOUND(4001, "RESOURCE_NOT_FOUND", "Không tìm thấy thông tin đăng ký khóa học", HttpStatus.NOT_FOUND),
    ALREADY_ENROLLED(4002, "DUPLICATE_RESOURCE", "Bạn đã đăng ký khóa học này trước đó", HttpStatus.CONFLICT),
    COURSE_NOT_PUBLISHED_FOR_ENROLL(4003, "INVALID_STATE_TRANSITION", "Khóa học chưa được xuất bản, không thể đăng ký", HttpStatus.BAD_REQUEST),
    LESSON_ALREADY_COMPLETED(4004, "DUPLICATE_RESOURCE", "Bài học đã được đánh dấu hoàn thành từ trước", HttpStatus.CONFLICT),
    LESSON_NOT_IN_COURSE(4005, "INVALID_INPUT_DATA", "Bài học không thuộc khóa học đã đăng ký", HttpStatus.BAD_REQUEST),

    // ================= NOTIFICATION =================
    NOTIFICATION_NOT_FOUND(5001, "RESOURCE_NOT_FOUND", "Không tìm thấy thông báo", HttpStatus.NOT_FOUND),
    NOTIFICATION_ACCESS_DENIED(5002, "ACCESS_DENIED", "Bạn không có quyền thao tác với thông báo này", HttpStatus.FORBIDDEN),

    // ================= REVIEW =================
    REVIEW_NOT_FOUND(6001, "RESOURCE_NOT_FOUND", "Không tìm thấy đánh giá", HttpStatus.NOT_FOUND),
    ALREADY_REVIEWED(6002, "DUPLICATE_RESOURCE", "Bạn đã gửi đánh giá cho khóa học này rồi", HttpStatus.CONFLICT),
    REVIEW_ACCESS_DENIED(6003, "ACCESS_DENIED", "Bạn không có quyền sửa hoặc xóa đánh giá này", HttpStatus.FORBIDDEN),
    INVALID_RATING(6004, "INVALID_INPUT_DATA", "Số sao đánh giá phải nằm trong khoảng từ 1 đến 5", HttpStatus.BAD_REQUEST),

    // ================= REPORT =================
    REPORT_DATA_NOT_FOUND(7001, "RESOURCE_NOT_FOUND", "Không tìm thấy dữ liệu báo cáo thống kê", HttpStatus.NOT_FOUND),

    // ================= HTTP & REQUEST VALIDATION =================
    INVALID_INPUT_DATA(8000, "INVALID_INPUT_DATA", "Dữ liệu đầu vào không hợp lệ", HttpStatus.BAD_REQUEST),
    MALFORMED_JSON(8001, "INVALID_INPUT_DATA", "Cấu trúc dữ liệu JSON gửi lên không hợp lệ", HttpStatus.BAD_REQUEST),
    METHOD_NOT_SUPPORTED(8002, "INVALID_INPUT_DATA", "Phương thức HTTP không được hỗ trợ cho Endpoint này", HttpStatus.METHOD_NOT_ALLOWED),
    TYPE_MISMATCH(8003, "INVALID_INPUT_DATA", "Tham số trên đường dẫn không đúng kiểu dữ liệu", HttpStatus.BAD_REQUEST),

    // ================= SYSTEM =================
    UNCATEGORIZED_EXCEPTION(9999, "INTERNAL_SERVER_ERROR", "Lỗi hệ thống chưa được phân loại", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String errorCode;
    private final String message;
    private final HttpStatus httpStatus;
}