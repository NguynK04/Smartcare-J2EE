package com.example.smartcare.service;

import com.example.smartcare.dto.AuthResponse;
import com.example.smartcare.dto.DoctorRegisterRequest;
import com.example.smartcare.dto.LoginRequest;
import com.example.smartcare.dto.RegisterRequest;
import com.example.smartcare.dto.UserResponse;
import com.example.smartcare.entity.User;
import com.example.smartcare.enums.Role;
import com.example.smartcare.repository.UserRepository;
import com.example.smartcare.security.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Công cụ băm Bcrypt đã cấu hình ở SecurityConfig
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @Transactional
    public AuthResponse login(LoginRequest request) {
        // 1. Giao cho "Bảo vệ" kiểm tra username và password. 
        // Nếu sai pass, nó sẽ tự động ném ra lỗi. Nếu đúng, nó cho đi tiếp.
        authenticationManager.authenticate(
                new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // 2. Lấy thông tin User từ Database
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại"));

        // 3. Đưa User vào "Máy in thẻ" để lấy Token
        var jwtToken = jwtService.generateToken(user);

        // 4. Trả Token + Role + Username về cho Client
        return AuthResponse.builder()
                .token(jwtToken)
                .type("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
    public UserResponse register(RegisterRequest request) {
        // 1. Kiểm tra trùng lặp
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng!");
        }

        // 2. Tạo User Entity, ép cứng Role mặc định là PATIENT (Bệnh nhân)
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // Băm mật khẩu tại đây
                .fullName(request.getFullName())
                .email(request.getEmail())
                .role(Role.PATIENT) // Đăng ký công khai luôn là Patient
                .isActive(true)
                .build();

        // 3. Lưu xuống Database
        User savedUser = userRepository.save(user);

        // 4. Trả về Response
        return UserResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }

    // ==========================================
    // ĐĂNG KÝ BÁC SĨ VỚI CV
    // ==========================================
    @Transactional
    public UserResponse registerDoctor(DoctorRegisterRequest request, String cvFilePath) {
        // 1. Kiểm tra trùng lặp
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Tên đăng nhập đã tồn tại!");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã được sử dụng!");
        }

        // 2. Tạo User Entity với Role DOCTOR
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .licenseNumber(request.getLicenseNumber())
                .cvFilePath(cvFilePath) // Lưu đường dẫn CV
                .role(Role.DOCTOR) // Đăng ký với role DOCTOR
                .isActive(false) // Ban đầu là inactive (chờ admin duyệt)
                .build();

        // 3. Lưu xuống Database
        User savedUser = userRepository.save(user);

        // 4. Trả về Response
        return UserResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .fullName(savedUser.getFullName())
                .email(savedUser.getEmail())
                .role(savedUser.getRole())
                .build();
    }
}