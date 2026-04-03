package com.example.smartcare.service;

import com.example.smartcare.dto.AppointmentRequest;
import com.example.smartcare.dto.AppointmentResponse;
import com.example.smartcare.entity.Appointment;
import com.example.smartcare.entity.Schedule;
import com.example.smartcare.entity.User;
import com.example.smartcare.enums.AppointmentStatus;
import com.example.smartcare.enums.Role;
import com.example.smartcare.repository.AppointmentRepository;
import com.example.smartcare.repository.ScheduleRepository;
import com.example.smartcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final EmailService emailService; 
    private final AppointmentRepository appointmentRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    // ==========================================
    // 1. BỆNH NHÂN ĐẶT LỊCH 
    // ==========================================
    @Transactional
    public AppointmentResponse bookAppointment(AppointmentRequest request) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User patient = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin bệnh nhân!"));

        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new RuntimeException("Lịch khám không tồn tại!"));

        if (schedule.isBooked()) {
            throw new RuntimeException("Lịch khám này đã có người đặt, vui lòng chọn ca khác!");
        }

        schedule.setBooked(true);
        scheduleRepository.save(schedule);

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .schedule(schedule)
                .reason(request.getReason())
                .status(AppointmentStatus.PENDING) 
                .createdAt(LocalDateTime.now())
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // ====================================================
        // BOSS 1: GỌI EMAIL SERVICE GỬI THÔNG BÁO TỰ ĐỘNG
        // ====================================================
        User doctor = schedule.getDoctor();
        
        // Gửi email cho BỆNH NHÂN
        if (patient.getEmail() != null && !patient.getEmail().isEmpty()) {
            emailService.sendAppointmentConfirmation(
                    patient.getEmail(), 
                    patient.getFullName(), 
                    doctor.getFullName(), 
                    schedule.getWorkDate().toString(), 
                    schedule.getStartTime().toString()
            );
        }
        
        // Gửi email cho BÁC SĨ
        if (doctor.getEmail() != null && !doctor.getEmail().isEmpty()) {
            emailService.sendDoctorNotificationEmail(
                    doctor.getEmail(),
                    doctor.getFullName(),
                    patient.getFullName(),
                    schedule.getWorkDate().toString(),
                    schedule.getStartTime().toString(),
                    savedAppointment.getReason()
            );
        }
        // ====================================================

        return AppointmentResponse.builder()
                .appointmentId(savedAppointment.getId())
                .patientName(patient.getFullName())
                .doctorName(doctor.getFullName()) 
                .workDate(schedule.getWorkDate())
                .startTime(schedule.getStartTime())
                .endTime(schedule.getEndTime())
                .reason(savedAppointment.getReason())
                .status(savedAppointment.getStatus())
                .build();
    }

    // ==========================================
    // 2. BÁC SĨ DUYỆT LỊCH
    // ==========================================
    @Transactional
    public AppointmentResponse updateAppointmentStatus(Long appointmentId, AppointmentStatus newStatus) {
        
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentDoctor = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng!"));

        if (currentDoctor.getRole() != Role.DOCTOR) {
            throw new RuntimeException("Lỗi phân quyền: Chỉ Bác sĩ mới được duyệt lịch khám!");
        }

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu khám bệnh!"));

        if (!appointment.getSchedule().getDoctor().getId().equals(currentDoctor.getId())) {
            throw new RuntimeException("Bạn không có quyền duyệt lịch khám của bác sĩ khác!");
        }

        AppointmentStatus currentStatus = appointment.getStatus();
        if (currentStatus == AppointmentStatus.CANCELLED || currentStatus == AppointmentStatus.COMPLETED) {
            throw new RuntimeException("Lỗi logic: Không thể đổi trạng thái phiếu khám đã Hủy hoặc đã Khám xong!");
        }
        if (currentStatus == newStatus) {
            throw new RuntimeException("Phiếu khám đang ở trạng thái này rồi!");
        }

        appointment.setStatus(newStatus);

        if (newStatus == AppointmentStatus.CANCELLED) {
            appointment.getSchedule().setBooked(false);
            scheduleRepository.save(appointment.getSchedule());
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);

        // ====================================================
        // GỬI EMAIL THÔNG BÁO KHI BÁC SĨ XÁC NHẬN HOẶC HỦY
        // ====================================================
        User patient = savedAppointment.getPatient();
        Schedule schedule = savedAppointment.getSchedule();
        
        if (patient.getEmail() != null && !patient.getEmail().isEmpty()) {
            if (newStatus == AppointmentStatus.CONFIRMED) {
                // Gửi email xác nhận
                emailService.sendAppointmentConfirmedEmail(
                        patient.getEmail(),
                        patient.getFullName(),
                        currentDoctor.getFullName(),
                        schedule.getWorkDate().toString(),
                        schedule.getStartTime().toString()
                );
            } else if (newStatus == AppointmentStatus.CANCELLED) {
                // Gửi email hủy lịch
                emailService.sendAppointmentCancelledEmail(
                        patient.getEmail(),
                        patient.getFullName(),
                        currentDoctor.getFullName(),
                        schedule.getWorkDate().toString(),
                        schedule.getStartTime().toString()
                );
            }
        }
        // ====================================================

        return AppointmentResponse.builder()
                .appointmentId(savedAppointment.getId())
                .patientName(savedAppointment.getPatient().getFullName())
                .doctorName(currentDoctor.getFullName())        
                .workDate(savedAppointment.getSchedule().getWorkDate())
                .startTime(savedAppointment.getSchedule().getStartTime())
                .endTime(savedAppointment.getSchedule().getEndTime())
                .reason(savedAppointment.getReason())
                .status(savedAppointment.getStatus())
                .build();
    }

    // ==========================================
    // 3. BỆNH NHÂN XEM LỊCH SỬ KHÁM
    // ==========================================
    public List<AppointmentResponse> getPatientAppointments() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User patient = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin bệnh nhân!"));

        List<Appointment> appointments = appointmentRepository.findByPatientOrderByCreatedAtDesc(patient);

        return appointments.stream().map(appt -> AppointmentResponse.builder()
                .appointmentId(appt.getId())
                .patientName(appt.getPatient().getFullName())
                .doctorName(appt.getSchedule().getDoctor().getFullName())
                .workDate(appt.getSchedule().getWorkDate())
                .startTime(appt.getSchedule().getStartTime())
                .endTime(appt.getSchedule().getEndTime())
                .reason(appt.getReason())
                .status(appt.getStatus())
                .build()
        ).toList();
    }

    // ==========================================
    // 4. BÁC SĨ XEM DANH SÁCH BỆNH NHÂN ĐẶT LỊCH
    // ==========================================
    public List<AppointmentResponse> getDoctorAppointments() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User doctor = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin bác sĩ!"));

        if (doctor.getRole() != Role.DOCTOR) {
            throw new RuntimeException("Chỉ Bác sĩ mới được xem danh sách này!");
        }

        List<Appointment> appointments = appointmentRepository.findByScheduleDoctorOrderByCreatedAtDesc(doctor);

        return appointments.stream().map(appt -> AppointmentResponse.builder()
                .appointmentId(appt.getId())
                .patientName(appt.getPatient().getFullName())
                .doctorName(doctor.getFullName())
                .workDate(appt.getSchedule().getWorkDate())
                .startTime(appt.getSchedule().getStartTime())
                .endTime(appt.getSchedule().getEndTime())
                .reason(appt.getReason())
                .status(appt.getStatus())
                .build()
        ).toList();
    }

    // ==========================================
    // 5. BỆNH NHÂN TỰ HỦY LỊCH KHÁM
    // ==========================================
    @Transactional
    public String cancelAppointmentByPatient(Long appointmentId) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        User patient = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin bệnh nhân!"));

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu khám bệnh!"));

        if (!appointment.getPatient().getId().equals(patient.getId())) {
            throw new RuntimeException("Lỗi bảo mật: Bạn không thể hủy lịch khám của người khác!");
        }

        if (appointment.getStatus() != AppointmentStatus.PENDING) {
            throw new RuntimeException("Lỗi logic: Chỉ có thể tự hủy lịch khi đang ở trạng thái Chờ duyệt (PENDING)!");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.getSchedule().setBooked(false);

        scheduleRepository.save(appointment.getSchedule());
        appointmentRepository.save(appointment);

        return "Hủy lịch khám thành công!";
    }

    // ==========================================
    // 6. LẤY CHI TIẾT LỊCH KHÁM
    // ==========================================
    public AppointmentResponse getAppointmentById(Long id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch khám với ID: " + id));

        return AppointmentResponse.builder()
                .appointmentId(appointment.getId())
                .patientName(appointment.getPatient().getFullName())
                .doctorName(appointment.getSchedule().getDoctor().getFullName())
                .workDate(appointment.getSchedule().getWorkDate())
                .startTime(appointment.getSchedule().getStartTime())
                .endTime(appointment.getSchedule().getEndTime())
                .reason(appointment.getReason())
                .status(appointment.getStatus())
                .build();
    }
}