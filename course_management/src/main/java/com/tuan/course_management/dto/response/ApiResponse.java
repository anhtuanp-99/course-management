package com.tuan.course_management.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
 * Class đóng gói cấu trúc phản hồi chuẩn cho toàn bộ API hệ thống.
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

    @Builder.Default
    private int code = 1000;
    private boolean success;
    private String message;
    private T data;

    /**
     * Tạo response thành công với dữ liệu trả về.
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(1000)
                .success(true)
                .data(data)
                .build();
    }

    /**
     * Tạo response thành công với thông điệp và dữ liệu trả về.
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .code(1000)
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Tạo response thất bại mặc định với thông điệp lỗi (Code mặc định 9999).
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .code(9999)
                .success(false)
                .message(message)
                .build();
    }

    /**
     * Tạo response thất bại với mã lỗi định danh (Custom Error Code) và thông điệp lỗi.
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .success(false)
                .message(message)
                .build();
    }
}