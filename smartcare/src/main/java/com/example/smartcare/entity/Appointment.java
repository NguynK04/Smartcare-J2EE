package com.example.smartcare.entity;

import com.example.smartcare.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Bệnh nhân nào đặt lịch?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient;

    // Đặt vào ca làm việc nào?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    // Lý do đến khám
    @Column(columnDefinition = "TEXT")
    private String reason;

    // Trạng thái cuộc hẹn
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    // Thời gian bấm nút tạo lịch trên hệ thống
    @Column(nullable = false)
    private LocalDateTime createdAt;
}