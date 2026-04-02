package com.example.smartcare.entity;

import com.example.smartcare.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User implements UserDetails {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    // ==========================================
    // MỐI QUAN HỆ VỚI CHUYÊN KHOA (MỚI THÊM VÀO)
    // ==========================================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id")
    private Specialty specialty;

    // Số Giấy Phép (dành cho bác sĩ)
    @Column(length = 50)
    private String licenseNumber;

    // Đường dẫn file CV (dành cho bác sĩ chưa duyệt)
    @Column(length = 500)
    private String cvFilePath;

    @Builder.Default
    private boolean isActive = true;

    // =========================================================
    // CÁC HÀM BẮT BUỘC CỦA SPRING SECURITY ĐỂ ĐỌC TÀI KHOẢN
    // =========================================================

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return isActive; } 
}