package com.tuan.course_management.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Quản lý tập trung toàn bộ mã lỗi, thông báo và HTTP Status của hệ thống.
 */
@Getter
public enum ErrorCode {

    // ================= AUTH & USER =================
    USER_NOT_FOUND(1001, "Không tìm thấy người dùng", HttpStatus.NOT_FOUND),
    EMAIL_ALREADY_EXISTS(1002, "Email đã được sử dụng", HttpStatus.CONFLICT),
    INVALID_PASSWORD(1003, "Mật khẩu không chính xác", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED_ACCESS(1004, "Bạn cần đăng nhập để truy cập", HttpStatus.UNAUTHORIZED),
    FORBIDDEN_RESOURCE(1005, "Bạn không có quyền thực hiện thao tác này", HttpStatus.FORBIDDEN),
    CANNOT_CHANGE_OWN_ROLE(1006, "Bạn không thể thay đổi vai trò của chính mình", HttpStatus.BAD_REQUEST),
    ACCOUNT_DISABLED(1007, "Tài khoản của bạn đã bị khóa hoặc chưa kích hoạt", HttpStatus.FORBIDDEN),
    INVALID_TOKEN(1008, "Token không hợp lệ hoặc đã hết hạn", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED(1009, "Token đã hết hạn", HttpStatus.UNAUTHORIZED),

    // ================= COURSE =================
    COURSE_NOT_FOUND(2001, "Không tìm thấy khóa học", HttpStatus.NOT_FOUND),
    COURSE_ALREADY_PUBLISHED(2002, "Khóa học đã được xuất bản", HttpStatus.BAD_REQUEST),
    COURSE_NOT_PUBLISHED(2003, "Khóa học chưa được xuất bản", HttpStatus.BAD_REQUEST),
    COURSE_HAS_ENROLLMENTS(2004, "Không thể xóa khóa học đã có học viên đăng ký", HttpStatus.CONFLICT),
    INVALID_COURSE_STATUS(2005, "Trạng thái khóa học không hợp lệ", HttpStatus.BAD_REQUEST),

    // ================= LESSON =================
    LESSON_NOT_FOUND(3001, "Không tìm thấy bài học", HttpStatus.NOT_FOUND),
    LESSON_ALREADY_PUBLISHED(3002, "Bài học đã được xuất bản", HttpStatus.BAD_REQUEST),
    LESSON_NOT_PUBLISHED(3003, "Bài học chưa được xuất bản", HttpStatus.BAD_REQUEST),
    LESSON_ACCESS_DENIED(3004, "Bạn không có quyền quản lý bài học này", HttpStatus.FORBIDDEN),

    // ================= ENROLLMENT =================
    ENROLLMENT_NOT_FOUND(4001, "Không tìm thấy đăng ký khóa học", HttpStatus.NOT_FOUND),
    ALREADY_ENROLLED(4002, "Bạn đã đăng ký khóa học này", HttpStatus.CONFLICT),
    COURSE_NOT_PUBLISHED_FOR_ENROLL(4003, "Khóa học chưa được xuất bản, không thể đăng ký", HttpStatus.BAD_REQUEST),
    LESSON_ALREADY_COMPLETED(4004, "Bài học đã được hoàn thành", HttpStatus.CONFLICT),
    LESSON_NOT_IN_COURSE(4005, "Bài học không thuộc khóa học đã đăng ký", HttpStatus.BAD_REQUEST),

    // ================= NOTIFICATION =================
    NOTIFICATION_NOT_FOUND(5001, "Không tìm thấy thông báo", HttpStatus.NOT_FOUND),
    NOTIFICATION_ACCESS_DENIED(5002, "Bạn không có quyền xóa thông báo này", HttpStatus.FORBIDDEN),

    // ================= REVIEW =================
    REVIEW_NOT_FOUND(6001, "Không tìm thấy đánh giá", HttpStatus.NOT_FOUND),
    ALREADY_REVIEWED(6002, "Bạn đã đánh giá khóa học này", HttpStatus.CONFLICT),
    REVIEW_ACCESS_DENIED(6003, "Bạn không có quyền sửa/xóa đánh giá này", HttpStatus.FORBIDDEN),
    INVALID_RATING(6004, "Số sao đánh giá phải từ 1 đến 5", HttpStatus.BAD_REQUEST),

    // ================= REPORT =================
    REPORT_DATA_NOT_FOUND(7001, "Không tìm thấy dữ liệu báo cáo", HttpStatus.NOT_FOUND),

    // ================= SYSTEM =================
    UNCATEGORIZED_EXCEPTION(9999, "Lỗi hệ thống chưa được phân loại", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}