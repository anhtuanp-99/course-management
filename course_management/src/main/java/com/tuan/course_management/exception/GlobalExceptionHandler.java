package com.tuan.course_management.exception;

import com.tuan.course_management.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
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
 * GlobalExceptionHandler: Bộ chặn và xử lý tập trung toàn bộ ngoại lệ trong ứng dụng.
 * Chuyển đổi tất cả lỗi hệ thống và nghiệp vụ về định dạng ApiResponse thống nhất.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Xử lý lỗi không tìm thấy tài nguyên.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Không tìm thấy tài nguyên: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Xử lý lỗi yêu cầu không hợp lệ từ logic nghiệp vụ.
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex) {
        log.warn("Yêu cầu không hợp lệ: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Xử lý lỗi chưa xác thực người dùng.
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnauthorized(UnauthorizedException ex) {
        log.warn("Chưa xác thực: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Xử lý lỗi không có quyền thực hiện hành động.
     */
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ApiResponse<Void>> handleForbidden(ForbiddenException ex) {
        log.warn("Không có quyền truy cập: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Xử lý lỗi xung đột dữ liệu.
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(ConflictException ex) {
        log.warn("Xung đột dữ liệu: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Xử lý lỗi sai tài khoản hoặc mật khẩu khi đăng nhập.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Đăng nhập thất bại: Sai email hoặc mật khẩu");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Email hoặc mật khẩu không chính xác"));
    }

    /**
     * Xử lý lỗi tài khoản bị khóa hoặc bị vô hiệu hóa.
     */
    @ExceptionHandler({DisabledException.class, LockedException.class})
    public ResponseEntity<ApiResponse<Void>> handleAccountDisabled(Exception ex) {
        log.warn("Đăng nhập thất bại: Tài khoản đang bị khóa");
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Tài khoản của bạn đã bị khóa hoặc chưa kích hoạt"));
    }

    /**
     * Xử lý lỗi từ chối truy cập từ phân quyền Spring Security (@PreAuthorize).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Truy cập bị từ chối: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("Bạn không có quyền thực hiện hành động này"));
    }

    /**
     * Xử lý lỗi Validation dữ liệu đầu vào Request Body (@Valid).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        log.warn("Lỗi dữ liệu đầu vào: {}", errors);
        ApiResponse<Map<String, String>> response = ApiResponse.error("Dữ liệu không hợp lệ");
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
                .body(ApiResponse.error(ex.getMessage()));
    }

    /**
     * Xử lý lỗi Body JSON sai cú pháp hoặc sai kiểu dữ liệu truyền vào.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Cấu trúc dữ liệu JSON không hợp lệ: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Dữ liệu định dạng JSON gửi lên không hợp lệ hoặc sai kiểu dữ liệu"));
    }

    /**
     * Xử lý lỗi tham số trên đường dẫn (URL) không đúng kiểu dữ liệu khai báo.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.warn("Tham số '{}' có giá trị '{}' không đúng kiểu dữ liệu yêu cầu", ex.getName(), ex.getValue());
        String message = String.format("Tham số '%s' phải thuộc kiểu dữ liệu %s",
                ex.getName(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "mới");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(message));
    }

    /**
     * Xử lý lỗi gọi sai phương thức HTTP (GET, POST, PUT, DELETE).
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.warn("Phương thức HTTP '{}' không được hỗ trợ cho Endpoint này", ex.getMethod());
        String message = String.format("Phương thức HTTP %s không được hỗ trợ cho đường dẫn này", ex.getMethod());
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(message));
    }

    /**
     * Xử lý tất cả các ngoại lệ chưa được phân loại (Lưới hứng cuối cùng).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobal(Exception ex) {
        log.error("Lỗi hệ thống chưa xác định: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Lỗi hệ thống nội bộ, vui lòng thử lại sau"));
    }
}