package com.example.smartcare.service;

import com.example.smartcare.dto.MedicalRecordRequest;
import com.example.smartcare.entity.Appointment;
import com.example.smartcare.entity.MedicalRecord;
import com.example.smartcare.entity.User;
import com.example.smartcare.enums.AppointmentStatus;
import com.example.smartcare.enums.Role;
import com.example.smartcare.repository.AppointmentRepository;
import com.example.smartcare.repository.MedicalRecordRepository;
import com.example.smartcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;

    @Transactional
    public String createMedicalRecord(Long appointmentId, MedicalRecordRequest request) {
        
        // 1. Kiểm tra Bác sĩ đang đăng nhập
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User doctor = userRepository.findByUsername(username).orElseThrow();

        if (doctor.getRole() != Role.DOCTOR) {
            throw new RuntimeException("Chỉ Bác sĩ mới được phép tạo hồ sơ bệnh án!");
        }

        // 2. Tìm Phiếu khám bệnh
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ca khám bệnh!"));

        // 3. Bảo mật: Bác sĩ A không được viết bệnh án cho bệnh nhân của Bác sĩ B
        if (!appointment.getSchedule().getDoctor().getId().equals(doctor.getId())) {
            throw new RuntimeException("Bạn không được phép khám cho bệnh nhân của bác sĩ khác!");
        }

        // 4. VAN BẢO VỆ (Sparring Partner Insight)
        // Nếu ca khám chưa được xác nhận (PENDING) hoặc đã hủy (CANCELLED), tuyệt đối không cho khám!
        if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
            throw new RuntimeException("Lỗi logic: Chỉ có thể viết bệnh án cho các ca khám đã được XÁC NHẬN (CONFIRMED)!");
        }

        // 5. Lưu Hồ sơ bệnh án vào bảng medical_records
        MedicalRecord record = MedicalRecord.builder()
                .appointment(appointment)
                .diagnosis(request.getDiagnosis())
                .prescription(request.getPrescription()) // JSON string: [{"name": "Thuốc A", "quantity": 2, "note": "2 lần/ngày"}]
                .notes(request.getNotes())
                .createdAt(LocalDateTime.now())
                .build();
        
        medicalRecordRepository.save(record);

        // 6. =====================================================
        // CẬP NHẬT TRẠNG THÁI CA KHÁM -> HOÀN THÀNH (COMPLETED)
        // =====================================================
        // SQL tương đương: UPDATE appointments SET status = 'COMPLETED' WHERE id = appointmentId
        
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        return "Đã lưu hồ sơ bệnh án và hoàn thành ca khám thành công!";
    }

    // API XEM BỆNH ÁN & ĐƠN THUỐC
    public com.example.smartcare.dto.MedicalRecordResponse getMedicalRecord(Long appointmentId) {
        // 1. Lấy thông tin người đang gọi API
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user!"));

        // 2. Lấy phiếu khám ra kiểm tra
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ca khám bệnh!"));

        // 3. VAN BẢO VỆ KÉP: Chỉ Bệnh nhân của ca khám này HOẶC Bác sĩ khám ca này mới được xem
        boolean isPatient = appointment.getPatient().getId().equals(currentUser.getId());
        boolean isDoctor = appointment.getSchedule().getDoctor().getId().equals(currentUser.getId());

        if (!isPatient && !isDoctor) {
            throw new RuntimeException("Lỗi bảo mật: Bạn không có quyền xem bệnh án của người khác!");
        }

        // 4. Lấy bệnh án
        MedicalRecord record = medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new RuntimeException("Ca khám này chưa có bệnh án (Bác sĩ chưa khám xong)!"));

        // 5. Trả về kết quả
        return com.example.smartcare.dto.MedicalRecordResponse.builder()
                .recordId(record.getId())
                .doctorName(appointment.getSchedule().getDoctor().getFullName())
                .diagnosis(record.getDiagnosis())
                .prescription(record.getPrescription())
                .notes(record.getNotes())
                .createdAt(record.getCreatedAt())
                .build();
    }
}