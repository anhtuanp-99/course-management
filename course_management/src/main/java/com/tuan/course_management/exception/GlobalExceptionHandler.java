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

import java.util.HashMap;
import java.util.Map;

/**
 * GlobalExceptionHandler: Bộ xử lý và chuẩn hóa toàn bộ ngoại lệ trong hệ thống.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Xử lý tất cả ngoại lệ nghiệp vụ do ứng dụng chủ động ném ra.
     * Đã cập nhật truyền cả Custom Error Code vào ApiResponse.
     */
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("Lỗi nghiệp vụ [Code {}]: {}", errorCode.getCode(), errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode.getCode(), errorCode.getMessage()));
    }

    /**
     * Xử lý lỗi sai email hoặc mật khẩu từ Spring Security.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Đăng nhập thất bại: Sai email hoặc mật khẩu");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ErrorCode.INVALID_PASSWORD.getCode(), "Email hoặc mật khẩu không chính xác"));
    }

    /**
     * Xử lý lỗi tài khoản bị vô hiệu hóa hoặc chưa kích hoạt.
     */
    @ExceptionHandler({DisabledException.class, LockedException.class})
    public ResponseEntity<ApiResponse<Void>> handleAccountDisabled(Exception ex) {
        log.warn("Đăng nhập thất bại: Tài khoản đang bị khóa");
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ErrorCode.FORBIDDEN_RESOURCE.getCode(), "Tài khoản của bạn đã bị khóa hoặc chưa kích hoạt"));
    }

    /**
     * Xử lý lỗi phân quyền Spring Security từ annotation @PreAuthorize.
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request) {

        log.warn("Truy cập bị từ chối do không đủ quyền tại URI: {} - Lý do: {}",
                request.getRequestURI(), ex.getMessage());

        ErrorCode errorCode = ErrorCode.FORBIDDEN_RESOURCE;
        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.error(errorCode.getCode(), errorCode.getMessage()));
    }

    /**
     * Xử lý lỗi Validation dữ liệu Request Body (@Valid).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        log.warn("Lỗi dữ liệu đầu vào: {}", errors);
        ApiResponse<Map<String, String>> response = ApiResponse.error(4000, "Dữ liệu không hợp lệ");
        response.setData(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Xử lý lỗi Validation tham số URL (@PathVariable, @RequestParam).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Lỗi tham số truy vấn: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(4000, ex.getMessage()));
    }

    /**
     * Xử lý lỗi Body JSON gửi lên sai cú pháp hoặc sai kiểu dữ liệu.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Cấu trúc dữ liệu JSON không hợp lệ: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(4001, "Dữ liệu định dạng JSON gửi lên không hợp lệ hoặc sai kiểu dữ liệu"));
    }

    /**
     * Xử lý lỗi tham số trên URL không đúng kiểu dữ liệu khai báo.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Tham số '{}' không đúng kiểu dữ liệu yêu cầu", ex.getName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(4002, "Tham số trên đường dẫn không đúng kiểu dữ liệu"));
    }


    /**
     * Xử lý lỗi gọi sai phương thức HTTP (GET, POST, PUT, DELETE).
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.warn("Phương thức HTTP '{}' không được hỗ trợ", ex.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(4003, "Phương thức HTTP không được hỗ trợ cho đường dẫn này"));
    }

    /**
     * Xử lý lỗi sắp xếp theo trường không tồn tại trong Entity (PropertyReferenceException).
     */
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ApiResponse<Void>> handlePropertyReference(PropertyReferenceException ex) {
        log.warn("Lỗi tham số sắp xếp không hợp lệ: Trường '{}' không tồn tại", ex.getPropertyName());
        String message = String.format("Trường sắp xếp '%s' không tồn tại trong hệ thống", ex.getPropertyName());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(4004, message));
    }

    /**
     * Lưới hứng cuối cùng cho tất cả các lỗi chưa được phân loại.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobal(Exception ex) {
        log.error("Lỗi hệ thống chưa xác định: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode(), "Lỗi hệ thống nội bộ, vui lòng thử lại sau"));
    }
}