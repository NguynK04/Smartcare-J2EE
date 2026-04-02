package com.example.smartcare.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Quan hệ 1-1: Một lần khám chỉ có 1 bệnh án duy nhất
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false, unique = true)
    private Appointment appointment;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String diagnosis; // Chẩn đoán bệnh (Bắt buộc)

    @Column(columnDefinition = "TEXT")
    private String prescription; // Đơn thuốc (Tùy chọn)

    @Column(columnDefinition = "TEXT")
    private String notes; // Lời dặn của bác sĩ (Tùy chọn)

    private LocalDateTime createdAt;
}