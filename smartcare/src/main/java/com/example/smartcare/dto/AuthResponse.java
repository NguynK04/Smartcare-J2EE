package com.example.smartcare.dto;

import com.example.smartcare.enums.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AuthResponse {
    private String token; // Chứa chuỗi JWT
    private String type;  // Thường là "Bearer"
    private Long userId;  // User ID để dùng cho chat
    private String username;
    private Role role;  // Vai trò (PATIENT, DOCTOR, ADMIN)
}