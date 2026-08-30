package com.tuan.course_management.exception;

import lombok.Getter;

/**
 * Ngoại lệ tùy chỉnh duy nhất dùng cho toàn bộ logic nghiệp vụ của ứng dụng.
 */
@Getter
public class AppException extends RuntimeException {

    private final ErrorCode errorCode;

    /**
     * Khởi tạo ngoại lệ với thông báo mặc định từ ErrorCode.
     */
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * Khởi tạo ngoại lệ với thông báo tùy biến (Custom Message) ghi đè thông báo mặc định.
     */
    public AppException(ErrorCode errorCode, String customMessage) {
        super(customMessage);
        this.errorCode = errorCode;
    }

    /**
     * Khởi tạo ngoại lệ kèm theo nguyên nhân gốc (Throwable cause).
     */
    public AppException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}