package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * NhanVienRepository
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 */
@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {

    /**
     * Tìm nhân viên theo tên đăng nhập
     *
     * @param tenDangNhap String
     * @return Optional<NhanVien>
     */
    Optional<NhanVien> findByTaiKhoan_TenDangNhap(String tenDangNhap);

    /**
     * Tìm danh sách nhân viên theo tên chức vụ
     *
     * @param tenChucVu String
     * @return List<NhanVien>
     */
    List<NhanVien> findByChucVu_TenChucVuContainingIgnoreCase(String tenChucVu);

    /**
     * Tìm nhân viên theo họ tên hoặc tên đăng nhập
     *
     * @param hoTen       String
     * @param tenDangNhap String
     * @return List<NhanVien>
     */
    List<NhanVien> findByHoTenContainingIgnoreCaseOrTaiKhoan_TenDangNhapContainingIgnoreCase(String hoTen, String tenDangNhap);
}