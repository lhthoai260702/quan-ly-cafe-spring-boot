package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * NhanVienRepository
 * * Version 1.2
 * * Date: 04-06-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 30-05-2026 Quản Lý      Apply Pagination (Pageable)
 * 04-06-2026 Quản Lý      Standardize Java Convention
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
     * Tìm danh sách nhân viên theo tên chức vụ (Có phân trang)
     *
     * @param tenChucVu String
     * @param pageable  Pageable
     * @return Page<NhanVien>
     */
    Page<NhanVien> findByChucVu_TenChucVuContainingIgnoreCase(String tenChucVu, Pageable pageable);

    /**
     * Tìm nhân viên theo họ tên hoặc tên đăng nhập (Có phân trang)
     *
     * @param hoTen       String
     * @param tenDangNhap String
     * @param pageable    Pageable
     * @return Page<NhanVien>
     */
    Page<NhanVien> findByHoTenContainingIgnoreCaseOrTaiKhoan_TenDangNhapContainingIgnoreCase(String hoTen, String tenDangNhap, Pageable pageable);
}