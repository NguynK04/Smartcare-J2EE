package com.example.smartcare.controller;

import com.example.smartcare.dto.ApiResponse;
import com.example.smartcare.dto.SpecialtyRequest;
import com.example.smartcare.dto.SpecialtyResponse;
import com.example.smartcare.service.SpecialtyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/specialties")
@RequiredArgsConstructor
public class SpecialtyController {

    private final SpecialtyService specialtyService;

    @PostMapping
    public ResponseEntity<ApiResponse<SpecialtyResponse>> createSpecialty(@Valid @RequestBody SpecialtyRequest request) {
        
        SpecialtyResponse data = specialtyService.createSpecialty(request);

        ApiResponse<SpecialtyResponse> response = ApiResponse.<SpecialtyResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Tạo chuyên khoa thành công!")
                .data(data)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // API: GET /api/v1/specialties/{id}/doctors
    @GetMapping("/{id}/doctors")
    public ResponseEntity<ApiResponse<java.util.List<com.example.smartcare.dto.DoctorResponse>>> getDoctorsBySpecialty(
            @PathVariable("id") Long specialtyId) {

        java.util.List<com.example.smartcare.dto.DoctorResponse> data = specialtyService.getDoctorsBySpecialty(specialtyId);

        ApiResponse<java.util.List<com.example.smartcare.dto.DoctorResponse>> response = ApiResponse.<java.util.List<com.example.smartcare.dto.DoctorResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách bác sĩ thành công!")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

    // API: GET /api/v1/specialties
    @GetMapping
    public ResponseEntity<ApiResponse<java.util.List<SpecialtyResponse>>> getAll() {
        java.util.List<SpecialtyResponse> data = specialtyService.getAllSpecialties();
        
        return ResponseEntity.ok(ApiResponse.<java.util.List<SpecialtyResponse>>builder()
                .code(200)
                .message("Lấy danh sách chuyên khoa thành công!")
                .data(data)
                .build());
    }
}