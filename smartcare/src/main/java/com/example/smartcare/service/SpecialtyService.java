package com.example.smartcare.service;

import com.example.smartcare.dto.SpecialtyRequest;
import com.example.smartcare.dto.SpecialtyResponse;
import com.example.smartcare.entity.Specialty;
import com.example.smartcare.entity.User;
import com.example.smartcare.enums.Role;
import com.example.smartcare.repository.SpecialtyRepository;
import com.example.smartcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SpecialtyService {

    private final SpecialtyRepository specialtyRepository;
    private final UserRepository userRepository;

    public SpecialtyResponse createSpecialty(SpecialtyRequest request) {
        // 1. Kiểm tra xem người đang gọi API có phải Admin không
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user!"));

        if (currentUser.getRole() != Role.ADMIN) {
            throw new RuntimeException("Lỗi phân quyền: Chỉ Quản trị viên (ADMIN) mới được tạo chuyên khoa!");
        }

        // 2. Tiến hành lưu vào Database
        Specialty specialty = Specialty.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Specialty savedSpecialty = specialtyRepository.save(specialty);

        // 3. Trả về kết quả
        return SpecialtyResponse.builder()
                .id(savedSpecialty.getId())
                .name(savedSpecialty.getName())
                .description(savedSpecialty.getDescription())
                .build();
    }

    // =======================================================
    // API CHO BỆNH NHÂN XEM DANH SÁCH BÁC SĨ THEO CHUYÊN KHOA
    // =======================================================
    public java.util.List<com.example.smartcare.dto.DoctorResponse> getDoctorsBySpecialty(Long specialtyId) {
        
        // 1. Kiểm tra xem Khoa này có tồn tại không
        Specialty specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên khoa!"));

        // 2. Nhờ Repository tìm tất cả Bác sĩ thuộc Khoa này
        java.util.List<User> doctors = userRepository.findBySpecialtyIdAndRole(specialtyId, Role.DOCTOR);

        // 3. Biến đổi dữ liệu Entity thành DTO trả về cho App
        return doctors.stream().map(doc -> com.example.smartcare.dto.DoctorResponse.builder()
                .doctorId(doc.getId())
                .fullName(doc.getFullName())
                .specialtyName(specialty.getName())
                .build()
        ).toList();
    }

    // Lấy danh sách tất cả chuyên khoa cho trang chủ
    public java.util.List<SpecialtyResponse> getAllSpecialties() {
        return specialtyRepository.findAll().stream()
                .map(s -> SpecialtyResponse.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .description(s.getDescription())
                        .build())
                .toList();
    }
}