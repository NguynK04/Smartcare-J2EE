package com.example.smartcare.controller;

import com.example.smartcare.service.UserService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Xử lý các request đến các trang HTML view
 * @Controller được enable để serve Thymeleaf templates
 * 
 * Spring Boot sẽ tự động map:
 * - "index" → /templates/index.html
 * - "patient/dashboard" → /templates/patient/dashboard.html
 * - v.v...
 */
@Controller
@RequiredArgsConstructor
public class ViewController {

    private final UserService userService;

    // ==================== PUBLIC PAGES ====================
    @PermitAll
    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PermitAll
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("doctors", userService.getAllDoctors());
        return "home";
    }

    @PermitAll
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PermitAll
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    // ==================== PATIENT PAGES ====================
    @GetMapping("/patient/dashboard")
    public String patientDashboard() {
        return "patient/dashboard";
    }

    @GetMapping("/patient/search-doctors")
    public String patientSearchDoctors() {
        return "patient/search-doctors";
    }

    @GetMapping("/patient/appointments")
    public String patientAppointments() {
        return "patient/appointments";
    }

    @GetMapping("/patient/profile")
    public String patientProfile() {
        return "patient/profile";
    }

    @GetMapping("/patient/review/{id}")
    public String patientReview(@PathVariable("id") Long appointmentId) {
        return "patient/review";
    }

    @GetMapping("/patient/book-appointment/{id}")
    public String patientBookAppointment() {
        return "patient/book-appointment";
    }

    // ==================== DOCTOR PAGES ====================
    @GetMapping("/doctor/dashboard")
    public String doctorDashboard() {
        return "doctor/dashboard";
    }

    @GetMapping("/doctor/schedules")
    public String doctorSchedules() {
        return "doctor/schedules";
    }

    @GetMapping("/doctor/patients")
    public String doctorPatients() {
        return "doctor/patients";
    }

    @GetMapping("/doctor/medical-record/{id}")
    public String doctorMedicalRecord(@PathVariable("id") Long id) {
        return "doctor/medical-record";
    }

    // ==================== ADMIN PAGES ====================
    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/doctors")
    public String adminDoctors() {
        return "admin/doctors";
    }

    @GetMapping("/admin/specialties")
    public String adminSpecialties() {
        return "admin/specialties";
    }

    @GetMapping("/admin/stats")
    public String adminStats() {
        return "admin/stats";
    }

    @GetMapping("/admin/patients")
    public String adminPatients() {
        return "admin/patients";
    }

    @GetMapping("/patient/appointment/{id}")
    public String patientAppointmentDetail(@PathVariable("id") Long id) {
        // Trả về file HTML xem chi tiết lịch khám của bệnh nhân
        return "patient/appointment-detail"; 
    }

    @GetMapping("/patient/medical-record-detail/{id}")
    public String patientMedicalRecordDetail(@PathVariable("id") Long appointmentId) {
        return "patient/medical-record-detail";
    }

    @GetMapping("/doctor/medical-record-view/{id}")
    public String doctorMedicalRecordView(@PathVariable("id") Long appointmentId) {
        return "doctor/medical-record-view";
    }

    // ==================== CHAT PAGES ====================
    @GetMapping("/patient/chat")
    public String patientChat() {
        return "patient/chat";
    }

    @GetMapping("/admin/customer-service")
    public String adminCustomerService() {
        return "admin/customer-service";
    }
}
