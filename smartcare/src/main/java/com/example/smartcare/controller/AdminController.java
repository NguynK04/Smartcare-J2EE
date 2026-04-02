package com.example.smartcare.controller;

import com.example.smartcare.dto.AdminDashboardResponse;
import com.example.smartcare.dto.ApiResponse;
import com.example.smartcare.service.DashboardService;
import com.example.smartcare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DashboardService dashboardService;
    private final UserService userService; 

    // API: GET /api/v1/admin/dashboard
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')") 
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.<AdminDashboardResponse>builder()
                .code(200)
                .message("Lấy dữ liệu thống kê Dashboard thành công!")
                .data(dashboardService.getAdminStats())
                .build());
    }

    // API: GET /api/v1/admin/profile - Lấy hồ sơ admin
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> getAdminProfile() {
        java.util.Map<String, Object> profile = new java.util.HashMap<>();
        profile.put("message", "Lấy hồ sơ admin thành công!");
        profile.put("data", "Admin profile placeholder");
        
        return ResponseEntity.ok(ApiResponse.<java.util.Map<String, Object>>builder()
                .code(200)
                .message("Lấy hồ sơ admin thành công!")
                .data(profile)
                .build());
    }

    // ==========================================
    // API: PUT /api/v1/admin/users/{userId}/toggle-status
    // ==========================================
    @PutMapping("/users/{userId}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> toggleUserStatus(@PathVariable("userId") Long userId) {
        String result = userService.toggleUserStatus(userId);
        
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .code(200)
                .message(result)
                .build());
    }

    // ==========================================
    // 🛠️ ĐÃ THÊM: API Lấy danh sách toàn bộ bác sĩ
    // ==========================================
    @GetMapping("/doctors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getAllDoctors() {
        // CHÚ Ý: Nếu hàm lấy danh sách bác sĩ trong UserService của ông tên khác thì sửa lại chỗ "getAllDoctors()" nhé!
        return ResponseEntity.ok(ApiResponse.builder()
                .code(200)
                .message("Lấy danh sách bác sĩ thành công!")
                .data(userService.getAllDoctors()) 
                .build());
    }
    // ==========================================
    // API: PUT /api/v1/admin/doctors/{id}/approve - Duyệt bác sĩ
    // ==========================================
    @PutMapping("/doctors/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> approveDoctor(@PathVariable("id") Long id) {
        
        // Gọi hàm xử lý duyệt từ Service
        userService.approveDoctor(id); 
        
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .code(200)
                .message("Duyệt hồ sơ bác sĩ thành công!")
                .build());
    }

    // ==========================================
    // API Lấy danh sách toàn bộ bệnh nhân
    // ==========================================
    @GetMapping("/patients")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Object>> getAllPatients() {
        return ResponseEntity.ok(ApiResponse.builder()
                .code(200)
                .message("Lấy danh sách bệnh nhân thành công!")
                .data(userService.getAllPatients()) 
                .build());
    }
}