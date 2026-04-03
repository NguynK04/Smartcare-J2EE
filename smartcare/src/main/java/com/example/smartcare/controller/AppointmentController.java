package com.example.smartcare.controller;

import com.example.smartcare.dto.ApiResponse;
import com.example.smartcare.dto.AppointmentRequest;
import com.example.smartcare.dto.AppointmentResponse;
// Đã xóa bỏ import UpdateAppointmentStatusRequest vì không dùng Body nữa
import com.example.smartcare.enums.AppointmentStatus; // Import cái Enum của ông vào đây
import com.example.smartcare.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // Bệnh nhân đặt lịch
    @PostMapping
    public ResponseEntity<ApiResponse<AppointmentResponse>> bookAppointment(@Valid @RequestBody AppointmentRequest request) {
        AppointmentResponse responseData = appointmentService.bookAppointment(request);

        ApiResponse<AppointmentResponse> response = ApiResponse.<AppointmentResponse>builder()
                .code(HttpStatus.CREATED.value())
                .message("Đặt lịch khám thành công! Vui lòng chờ bác sĩ xác nhận.")
                .data(responseData)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Bác sĩ duyệt lịch
    // 🛠️ ĐÃ SỬA: Bỏ @RequestBody, chuyển sang dùng @RequestParam để hứng cái '?status=CONFIRMED' từ URL
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") AppointmentStatus status) { // Spring Boot tự động ép kiểu từ chữ sang Enum

        // Truyền thẳng trạng thái vào service
        AppointmentResponse responseData = appointmentService.updateAppointmentStatus(id, status);

        ApiResponse<AppointmentResponse> response = ApiResponse.<AppointmentResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Cập nhật trạng thái thành công!")
                .data(responseData)
                .build();

        return ResponseEntity.ok(response);
    }

    // API Bệnh nhân xem lịch sử khám
    @GetMapping("/my-history")
    public ResponseEntity<ApiResponse<java.util.List<AppointmentResponse>>> getMyHistory() {
        
        java.util.List<AppointmentResponse> data = appointmentService.getPatientAppointments();

        ApiResponse<java.util.List<AppointmentResponse>> response = ApiResponse.<java.util.List<AppointmentResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy lịch sử khám bệnh thành công!")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

    // API: GET /api/v1/appointments/{id} - Lấy chi tiết appointment
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AppointmentResponse>> getAppointmentDetail(@PathVariable("id") Long id) {
        AppointmentResponse data = appointmentService.getAppointmentById(id);

        ApiResponse<AppointmentResponse> response = ApiResponse.<AppointmentResponse>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy chi tiết lịch khám thành công!")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

    // API: GET /api/v1/appointments/doctor/patients - Bác sĩ xem danh sách bệnh nhân 
    @GetMapping("/doctor/patients")
    public ResponseEntity<ApiResponse<java.util.List<AppointmentResponse>>> getDoctorPatients() {
        
        java.util.List<AppointmentResponse> data = appointmentService.getDoctorAppointments();

        ApiResponse<java.util.List<AppointmentResponse>> response = ApiResponse.<java.util.List<AppointmentResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách bệnh nhân thành công!")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

    // API Bác sĩ xem danh sách lịch hẹn
    @GetMapping("/doctor-requests")
    public ResponseEntity<ApiResponse<java.util.List<AppointmentResponse>>> getDoctorRequests() {
        
        java.util.List<AppointmentResponse> data = appointmentService.getDoctorAppointments();

        ApiResponse<java.util.List<AppointmentResponse>> response = ApiResponse.<java.util.List<AppointmentResponse>>builder()
                .code(HttpStatus.OK.value())
                .message("Lấy danh sách lịch hẹn thành công!")
                .data(data)
                .build();

        return ResponseEntity.ok(response);
    }

    // API: PUT /api/v1/appointments/{id}/cancel
    // Bệnh nhân tự hủy lịch
    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelAppointment(@PathVariable("id") Long appointmentId) {
        
        String message = appointmentService.cancelAppointmentByPatient(appointmentId);
        
        ApiResponse<String> response = ApiResponse.<String>builder()
                .code(HttpStatus.OK.value())
                .message(message)
                .build();

        return ResponseEntity.ok(response);
    }
}