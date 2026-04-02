package com.example.smartcare.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "specialties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Specialty {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Tên khoa (VD: Khoa Nội, Khoa Nhi, Răng Hàm Mặt)

    @Column(columnDefinition = "TEXT")
    private String description; // Mô tả chuyên khoa
}