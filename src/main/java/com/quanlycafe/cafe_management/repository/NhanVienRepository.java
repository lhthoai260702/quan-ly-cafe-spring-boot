package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.NhanVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * NhanVienRepository
 * * Version 1.3
 * * Date: 07-06-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 * 30-05-2026 lhthoai      Apply Pagination (Pageable)
 * 04-06-2026 lhthoai      Standardize Java Convention
 * 07-06-2026 lhthoai      Add findByChucVu_MaChucVu for dynamic filtering
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
     * Tìm danh sách nhân viên theo mã chức vụ (Có phân trang)
     *
     * @param maChucVu Integer
     * @param pageable Pageable
     * @return Page<NhanVien>
     */
    Page<NhanVien> findByChucVu_MaChucVu(Integer maChucVu, Pageable pageable);

    /**
     * Tìm danh sách nhân viên theo tên chức vụ (Có phân trang) - Đã giữ lại để dự phòng
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