package com.example.smartcare.controller;

import com.example.smartcare.dto.ApiResponse;
import com.example.smartcare.dto.MedicalRecordRequest;
import com.example.smartcare.service.MedicalRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    /**
     * API: POST /api/v1/medical-records
     * Lưu ghi chép y tế (hồ sơ bệnh án) cho một ca khám
     * 
     * Request body: {
     *   "appointmentId": 123,
     *   "diagnosis": "Cảm cúm",
     *   "prescription": "[{\"name\": \"Thuốc A\", \"quantity\": 2, \"note\": \"2 lần/ngày\"}]",
     *   "notes": "Uống nhiều nước"
     * }
     */
    @PostMapping("/medical-records")
    public ResponseEntity<ApiResponse<String>> createMedicalRecord(
            @Valid @RequestBody MedicalRecordRequest request) {

        // Lấy appointmentId từ request body
        String message = medicalRecordService.createMedicalRecord(request.getAppointmentId(), request);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(201) // Created
                .message(message)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * API: GET /api/v1/appointments/{id}/medical-records
     * Lấy hồ sơ bệnh án của một ca khám (để xem lại)
     */
    @GetMapping("/appointments/{id}/medical-records")
    public ResponseEntity<ApiResponse<com.example.smartcare.dto.MedicalRecordResponse>> getRecord(
            @PathVariable("id") Long appointmentId) {

        com.example.smartcare.dto.MedicalRecordResponse data = medicalRecordService.getMedicalRecord(appointmentId);

        ApiResponse<com.example.smartcare.dto.MedicalRecordResponse> response = ApiResponse.<com.example.smartcare.dto.MedicalRecordResponse>builder()
                .code(200)
                .message("Lấy bệnh án thành công!")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }
}