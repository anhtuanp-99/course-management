package com.tuan.course_management.exception;

import com.tuan.course_management.dto.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bộ xử lý và chuẩn hóa toàn bộ ngoại lệ phát sinh trong hệ thống về cấu trúc ApiResponse Envelope.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Xử lý ngoại lệ nghiệp vụ do ứng dụng chủ động throw.
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        // Dùng ex.getMessage() để ghi lại đúng thông điệp thực tế của exception phát sinh
        log.warn("Lỗi nghiệp vụ [{}]: {}", errorCode.getErrorCode(), ex.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode, ex.getMessage()));
    }

    /**
     * Xử lý lỗi sai tài khoản hoặc mật khẩu từ Spring Security.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Đăng nhập thất bại: Sai thông tin tài khoản hoặc mật khẩu");
        ErrorCode errorCode = ErrorCode.INVALID_PASSWORD;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode));
    }

    /**
     * Xử lý lỗi tài khoản bị vô hiệu hóa hoặc bị khóa.
     */
    @ExceptionHandler({DisabledException.class, LockedException.class})
    public ResponseEntity<ApiResponse<Void>> handleAccountDisabled(Exception ex) {
        log.warn("Truy cập bị từ chối: Tài khoản bị vô hiệu hóa");
        ErrorCode errorCode = ErrorCode.ACCOUNT_DISABLED;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode));
    }

    /**
     * Xử lý lỗi phân quyền truy cập.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        log.warn("Từ chối truy cập URI: {} - Lý do: {}", request.getRequestURI(), ex.getMessage());
        ErrorCode errorCode = ErrorCode.FORBIDDEN_RESOURCE;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode));
    }

    /**
     * Xử lý lỗi Validation dữ liệu Request Body (@Valid).
     * Trả về danh sách chi tiết các trường bị lỗi.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            Map<String, String> errMap = new HashMap<>();
            errMap.put("field", error.getField());
            errMap.put("message", error.getDefaultMessage());
            errors.add(errMap);
        });

        log.warn("Lỗi dữ liệu đầu vào (Validation): {}", errors);
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_DATA;

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode, errors));
    }

    /**
     * Xử lý lỗi Constraint Violation trên URL params (@PathVariable, @RequestParam).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Lỗi tham số truy vấn: {}", ex.getMessage());
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_DATA;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode, ex.getMessage()));
    }

    /**
     * Xử lý lỗi định dạng JSON gửi lên bị sai cú pháp.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Cấu trúc JSON không hợp lệ: {}", ex.getMessage());
        ErrorCode errorCode = ErrorCode.MALFORMED_JSON;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode));
    }

    /**
     * Xử lý lỗi tham số URL sai kiểu dữ liệu.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Tham số '{}' sai kiểu dữ liệu", ex.getName());
        ErrorCode errorCode = ErrorCode.TYPE_MISMATCH;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode));
    }

    /**
     * Xử lý lỗi gọi sai phương thức HTTP (GET, POST, PUT, DELETE).
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.warn("Phương thức HTTP '{}' không được hỗ trợ", ex.getMethod());
        ErrorCode errorCode = ErrorCode.METHOD_NOT_SUPPORTED;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode));
    }


    /**
     * Xử lý lỗi tham số sắp xếp không tồn tại trong Entity.
     */
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ApiResponse<Void>> handlePropertyReference(PropertyReferenceException ex) {
        log.warn("Trường sắp xếp '{}' không tồn tại", ex.getPropertyName());
        ErrorCode errorCode = ErrorCode.INVALID_INPUT_DATA;
        String message = String.format("Trường sắp xếp '%s' không tồn tại trong hệ thống", ex.getPropertyName());
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode, message));
    }

    /**
     * Xử lý lỗi gọi sai đường dẫn API không tồn tại (404 Not Found).
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFound(NoHandlerFoundException ex) {
        log.warn("Đường dẫn API không tồn tại: {} {}", ex.getHttpMethod(), ex.getRequestURL());

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .statusCode(HttpStatus.NOT_FOUND.value())
                .errorCode("RESOURCE_NOT_FOUND")
                .message("Đường dẫn API không tồn tại hoặc không đúng định dạng")
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Xử lý lỗi vi phạm ràng buộc dữ liệu toàn vẹn trong Database (ví dụ: Foreign Key Constraint khi xóa).
     */
    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(
            org.springframework.dao.DataIntegrityViolationException ex) {

        log.warn("Không thể thao tác dữ liệu do vướng ràng buộc quan hệ Database: {}", ex.getMessage());

        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .success(false)
                .statusCode(HttpStatus.CONFLICT.value()) // HTTP 409 Conflict
                .errorCode("DATA_INTEGRITY_VIOLATION")
                .message("Không thể xóa hoặc thay đổi dữ liệu này vì đang có các dữ liệu liên quan khác trong hệ thống")
                .timestamp(java.time.LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Lưới hứng cuối cùng cho các lỗi hệ thống chưa được phân loại (500).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobal(Exception ex) {
        log.error("Lỗi hệ thống chưa xác định: ", ex);
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(errorCode));
    }
}