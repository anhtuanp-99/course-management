package com.tuan.course_management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tuan.course_management.exception.ErrorCode;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Class đóng gói cấu trúc phản hồi chuẩn (Envelope Response) cho toàn bộ REST API hệ thống.
 * Đáp ứng 100% tài liệu đặc tả SRS Section 6.1.
 *
 * @param <T> Kiểu dữ liệu của phần dữ liệu phản hồi (Payload)
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private int statusCode;
    private String errorCode;
    private String message;
    private T data;
    private Object errors;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    // ================= FACTORY METHODS CHO THÀNH CÔNG =================

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(200)
                .message("Thao tác thành công")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(int statusCode, String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .statusCode(statusCode)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // ================= FACTORY METHODS CHO THẤT BẠI / LỖI =================

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return ApiResponse.<T>builder()
                .success(false)
                .statusCode(errorCode.getHttpStatus().value())
                .errorCode(errorCode.getErrorCode())
                .message(errorCode.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, String customMessage) {
        return ApiResponse.<T>builder()
                .success(false)
                .statusCode(errorCode.getHttpStatus().value())
                .errorCode(errorCode.getErrorCode())
                .message(customMessage)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode, Object errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .statusCode(errorCode.getHttpStatus().value())
                .errorCode(errorCode.getErrorCode())
                .message(errorCode.getMessage())
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}