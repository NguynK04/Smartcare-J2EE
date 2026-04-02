package com.example.smartcare.exception;

import com.example.smartcare.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Bắt lỗi Validation (Ví dụ: @FutureOrPresent, @NotNull)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Lấy thông báo lỗi đầu tiên cấu hình trong DTO
        String errorMessage = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .message(errorMessage)
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 2. Bắt lỗi 404 Đường dẫn
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFoundException(NoHandlerFoundException ex) {
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.NOT_FOUND.value())
                .message("Đường dẫn API không tồn tại: " + ex.getRequestURL())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // 3. Bắt mọi lỗi Logic nghiệp vụ (Ví dụ: Giờ kết thúc < Giờ bắt đầu)
    // 3. Bắt mọi lỗi Logic nghiệp vụ và trả về 400 (Bad Request) thay vì 500
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        // Tạm thời tắt ex.printStackTrace() đi để Terminal sạch sẽ, không xả rác nữa
        
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .code(HttpStatus.BAD_REQUEST.value()) // Nâng cấp: Đổi 500 thành 400
                .message(ex.getMessage()) // Nâng cấp: Trả nguyên văn câu báo lỗi, bỏ chữ "Lỗi hệ thống: "
                .build();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}