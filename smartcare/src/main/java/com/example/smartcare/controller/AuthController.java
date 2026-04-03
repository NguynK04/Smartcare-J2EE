package com.example.smartcare.controller;

import com.example.smartcare.dto.ApiResponse;
import com.example.smartcare.dto.AuthResponse;
import com.example.smartcare.dto.DoctorRegisterRequest;
import com.example.smartcare.dto.LoginRequest;
import com.example.smartcare.dto.RegisterRequest;
import com.example.smartcare.dto.UserResponse;
import com.example.smartcare.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse newUser = authService.register(request);

        ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Đăng ký tài khoản thành công")
                .data(newUser)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Đăng ký BÁC SĨ với CV (multipart/form-data)
    @PostMapping(value = "/register-doctor", consumes = {"multipart/form-data"})
    public ResponseEntity<ApiResponse<UserResponse>> registerDoctor(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("licenseNumber") String licenseNumber,
            @RequestParam("specialtyId") Long specialtyId,
            @RequestParam("cvFile") MultipartFile cvFile) {
        
        try {
            // Tạo thư mục lưu CV nếu chưa tồn tại
            String uploadDir = "uploads/cv";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Lưu file CV với tên duy nhất (sử dụng timestamp)
            String fileName = "cv_" + Instant.now().toEpochMilli() + "_" + cvFile.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(cvFile.getInputStream(), filePath);

            // Tạo request object
            DoctorRegisterRequest request = new DoctorRegisterRequest();
            request.setUsername(username);
            request.setPassword(password);
            request.setFullName(fullName);
            request.setEmail(email);
            request.setLicenseNumber(licenseNumber);
            request.setSpecialtyId(specialtyId);

            // Đăng ký doctor
            UserResponse newDoctor = authService.registerDoctor(request, filePath.toString());

            ApiResponse<UserResponse> response = ApiResponse.<UserResponse>builder()
                    .code(HttpStatus.CREATED.value())
                    .message("Đăng ký bác sĩ thành công! Vui lòng chờ Admin duyệt hồ sơ.")
                    .data(newDoctor)
                    .build();

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IOException e) {
            ApiResponse<UserResponse> errorResponse = ApiResponse.<UserResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Lỗi khi upload file CV: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            ApiResponse<UserResponse> errorResponse = ApiResponse.<UserResponse>builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("Lỗi đăng ký bác sĩ: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);

        ApiResponse<AuthResponse> response = ApiResponse.<AuthResponse>builder()
                .code(org.springframework.http.HttpStatus.OK.value())
                .message("Đăng nhập thành công")
                .data(authResponse)
                .build();

        return ResponseEntity.ok(response);
    }
}