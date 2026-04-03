package com.example.smartcare.service;

import com.example.smartcare.entity.Specialty;
import com.example.smartcare.entity.User;
import com.example.smartcare.enums.Role;
import com.example.smartcare.repository.SpecialtyRepository;
import com.example.smartcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SpecialtyRepository specialtyRepository;
    private final EmailService emailService;

    @Transactional
    public String assignSpecialtyToDoctor(Long doctorId, Long specialtyId) {
        
        // 1. Kiểm tra quyền Admin
        String adminUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin Admin!"));

        if (admin.getRole() != Role.ADMIN) {
            throw new RuntimeException("Lỗi phân quyền: Chỉ Admin mới được sắp xếp chuyên khoa!");
        }

        // 2. Tìm Bác sĩ cần phân công
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng này!"));

        if (doctor.getRole() != Role.DOCTOR) {
            throw new RuntimeException("Người dùng này không phải là Bác sĩ!");
        }

        // 3. Tìm Chuyên khoa
        Specialty specialty = specialtyRepository.findById(specialtyId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên khoa!"));

        // 4. "Đeo thẻ" chuyên khoa cho Bác sĩ và lưu lại
        doctor.setSpecialty(specialty);
        userRepository.save(doctor);

        return "Đã phân công bác sĩ " + doctor.getFullName() + " vào " + specialty.getName() + " thành công!";
    }

    // ==========================================
    // TÍNH NĂNG MỚI: ADMIN KHÓA / MỞ KHÓA TÀI KHOẢN
    // ==========================================
    @Transactional
    public String toggleUserStatus(Long userId) {
        // 1. Kiểm tra quyền Admin
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Admin!"));

        if (admin.getRole() != Role.ADMIN) {
            throw new RuntimeException("Lỗi phân quyền: Chỉ Admin mới có quyền khóa/mở khóa tài khoản!");
        }

        // 2. Tìm tài khoản bị "lên thớt"
        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng này!"));

        // 3. Van an toàn: Admin không được tự sát
        if (targetUser.getId().equals(admin.getId())) {
            throw new RuntimeException("Lỗi logic: Admin không thể tự khóa tài khoản của chính mình!");
        }

        // 4. Lật ngược trạng thái (Đang true -> false, Đang false -> true)
        boolean newStatus = !targetUser.isActive();
        targetUser.setActive(newStatus);
        userRepository.save(targetUser);

        String action = newStatus ? "MỞ KHÓA" : "KHÓA";
        return "Đã " + action + " tài khoản [" + targetUser.getUsername() + "] thành công!";
    }

    public java.util.List<User> searchDoctors(String keyword) {
        // Lấy tất cả bác sĩ active, sau đó filter theo keyword
        var allActiveDoctors = userRepository.findByRole(Role.DOCTOR)
                .stream()
                .filter(User::isActive)
                .toList();
        
        // Filter by keyword (tên hoặc username)
        return allActiveDoctors.stream()
                .filter(d -> d.getFullName().toLowerCase().contains(keyword.toLowerCase()) || 
                           d.getUsername().toLowerCase().contains(keyword.toLowerCase()))
                .toList();
    }

    // Lấy thông tin user theo ID
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user với ID: " + id));
    }

    // Lấy danh sách tất cả bác sĩ
    public java.util.List<User> getAllDoctors() {
        return userRepository.findByRole(Role.DOCTOR);
    }

    // ==========================================
    // Hàm duyệt hồ sơ bác sĩ
    // ==========================================
    public void approveDoctor(Long doctorId) {
        // 1. Tìm bác sĩ trong Database
        // Giả định ông dùng UserRepository và Entity tên là User
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với ID này"));

        // 2. Kiểm tra xem người này có đúng là Bác sĩ không
        if (!"DOCTOR".equals(doctor.getRole().name())) {
            throw new RuntimeException("Người dùng này không phải là bác sĩ");
        }

        // 3. Cập nhật trạng thái duyệt
        // CHÚ Ý: Chỗ này phụ thuộc vào Entity User của ông đặt tên biến là gì.
        // Dựa vào HTML lúc trước, tôi đoán ông có biến "verified" hoặc "isActive".
        // Nếu Entity của ông đặt tên khác (ví dụ: setStatus, setIsVerified...), hãy sửa lại cho đúng nhé!
        
        doctor.setActive(true); // Hoặc doctor.setIsActive(true);

        // 4. Lưu lại xuống Database
        userRepository.save(doctor);

        // ====================================================
        // GỬI EMAIL THÔNG BÁO BÁC SĨ ĐÃ ĐƯỢC DUYỆT
        // ====================================================
        if (doctor.getEmail() != null && !doctor.getEmail().isEmpty()) {
            emailService.sendDoctorApprovalEmail(
                    doctor.getEmail(),
                    doctor.getFullName()
            );
        }
        // ==================================================== 
    }

    // Lấy danh sách tất cả bệnh nhân
    public java.util.List<User> getAllPatients() {
        return userRepository.findByRole(Role.PATIENT);
    }

    // Lấy danh sách bác sĩ theo chuyên khoa
    public java.util.List<User> getDoctorsBySpecialty(Long specialtyId) {
        if (specialtyId == null || specialtyId <= 0) {
            return getAllDoctors();
        }
        
        return userRepository.findBySpecialtyIdAndRole(specialtyId, Role.DOCTOR);
    }
}