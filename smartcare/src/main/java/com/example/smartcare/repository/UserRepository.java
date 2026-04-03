package com.example.smartcare.repository;

import com.example.smartcare.entity.User;
import com.example.smartcare.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Dùng để đăng nhập
    Optional<User> findByUsername(String username);
    
    // Dùng cho AuthService kiểm tra trùng lặp lúc đăng ký
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    
    // Dùng để lấy danh sách Bác sĩ theo Chuyên khoa
    List<User> findBySpecialtyIdAndRole(Long specialtyId, Role role);

    // Lấy danh sách users theo role
    List<User> findByRole(Role role);

    // Tìm bác sĩ theo tên HOẶC triệu chứng/kinh nghiệm (mô tả)
    @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE u.role = 'DOCTOR' AND u.isActive = true " +
            "AND (u.fullName LIKE %:keyword% OR u.username LIKE %:keyword%)")
    java.util.List<User> searchDoctors(String keyword);
}