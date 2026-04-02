package com.example.smartcare.controller;

import com.example.smartcare.dto.ApiResponse;
import com.example.smartcare.dto.DoctorSearchResponse; // Nhớ check xem bro đã tạo file DTO này chưa
import com.example.smartcare.service.UserService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 1. API: Lấy thông tin profile user
    @GetMapping("/api/v1/users/{id}")
    public ResponseEntity<ApiResponse<DoctorSearchResponse>> getUser(@PathVariable("id") Long id) {
        var user = userService.getUserById(id);
        
        DoctorSearchResponse result = DoctorSearchResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .specialtyName(user.getSpecialty() != null ? user.getSpecialty().getName() : "Chưa có")
                .build();

        return ResponseEntity.ok(ApiResponse.<DoctorSearchResponse>builder()
                .code(200)
                .message("Lấy thông tin user thành công!")
                .data(result)
                .build());
    }
    
    // 2. API: List all doctors (cho Admin) - PUBLIC ACCESS
    @PermitAll
    @GetMapping("/api/v1/users/doctors/list")
    public ResponseEntity<ApiResponse<List<DoctorSearchResponse>>> getAllDoctors() {
        
        var doctors = userService.getAllDoctors();
        
        List<DoctorSearchResponse> result = doctors.stream().map(d -> DoctorSearchResponse.builder()
                .id(d.getId())
                .fullName(d.getFullName())
                .specialtyName(d.getSpecialty() != null ? d.getSpecialty().getName() : "Chưa có chuyên khoa")
                .build()
        ).toList();

        return ResponseEntity.ok(ApiResponse.<List<DoctorSearchResponse>>builder()
                .code(200)
                .message("Lấy danh sách bác sĩ thành công!")
                .data(result)
                .build());
    }

    // 3. API: Phân chuyên khoa cho Bác sĩ (Admin dùng)
    @PutMapping("/api/v1/users/doctors/{doctorId}/specialties/{specialtyId}")
    public ResponseEntity<ApiResponse<String>> assignSpecialty(
            @PathVariable("doctorId") Long doctorId,
            @PathVariable("specialtyId") Long specialtyId) {

        String message = userService.assignSpecialtyToDoctor(doctorId, specialtyId);

        return ResponseEntity.ok(ApiResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build());
    }

    // 4. API: Search Bác sĩ (Công khai - Bệnh nhân dùng)
    // Route mới: /api/v1/doctors/search để JavaScript gọi được
    @PermitAll
    @GetMapping("/api/v1/doctors/search")
    public ResponseEntity<ApiResponse<List<DoctorSearchResponse>>> searchFromDoctorPath(@RequestParam("keyword") String keyword) {
        
        var doctors = userService.searchDoctors(keyword);
        
        List<DoctorSearchResponse> result = doctors.stream().map(d -> DoctorSearchResponse.builder()
                .id(d.getId())
                .fullName(d.getFullName())
                .specialtyName(d.getSpecialty() != null ? d.getSpecialty().getName() : "Chưa có chuyên khoa")
                .build()
        ).toList();

        return ResponseEntity.ok(ApiResponse.<List<DoctorSearchResponse>>builder()
                .code(200)
                .message("Tìm thấy " + result.size() + " bác sĩ phù hợp!")
                .data(result)
                .build());
    }

    // 5. API: Search Bác sĩ (Công khai - Bệnh nhân dùng) - Original route
    @PermitAll
    @GetMapping("/api/v1/users/doctors/search")
    public ResponseEntity<ApiResponse<List<DoctorSearchResponse>>> search(@RequestParam("keyword") String keyword) {
        
        var doctors = userService.searchDoctors(keyword);
        
        List<DoctorSearchResponse> result = doctors.stream().map(d -> DoctorSearchResponse.builder()
                .id(d.getId())
                .fullName(d.getFullName())
                .specialtyName(d.getSpecialty() != null ? d.getSpecialty().getName() : "Chưa có chuyên khoa")
                .build()
        ).toList();

        return ResponseEntity.ok(ApiResponse.<List<DoctorSearchResponse>>builder()
                .code(200)
                .message("Tìm thấy " + result.size() + " bác sĩ phù hợp!")
                .data(result)
                .build());
    }

    // 6. API: Get doctors by specialty (New)
    @PermitAll
    @GetMapping("/api/v1/doctors/by-specialty")
    public ResponseEntity<ApiResponse<List<DoctorSearchResponse>>> getDoctorsBySpecialty(
            @RequestParam(value = "specialtyId", required = false) Long specialtyId) {
        
        var doctors = specialtyId != null && specialtyId > 0 
                ? userService.getDoctorsBySpecialty(specialtyId) 
                : userService.getAllDoctors();
        
        List<DoctorSearchResponse> result = doctors.stream().map(d -> DoctorSearchResponse.builder()
                .id(d.getId())
                .fullName(d.getFullName())
                .specialtyName(d.getSpecialty() != null ? d.getSpecialty().getName() : "Chưa có chuyên khoa")
                .build()
        ).toList();

        return ResponseEntity.ok(ApiResponse.<List<DoctorSearchResponse>>builder()
                .code(200)
                .message("Tìm thấy " + result.size() + " bác sĩ!")
                .data(result)
                .build());
    }
}