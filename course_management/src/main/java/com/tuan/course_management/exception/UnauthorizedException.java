package com.tuan.course_management.exception;

/**
 * Ngoại lệ ném ra khi người dùng chưa đăng nhập hoặc token không hợp lệ.
 * Thường được dùng trong filter hoặc service khi xác thực thất bại.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }
}