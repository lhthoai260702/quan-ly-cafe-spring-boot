package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {
    Optional<NhanVien> findByTaiKhoan_TenDangNhap(String tenDangNhap);
}