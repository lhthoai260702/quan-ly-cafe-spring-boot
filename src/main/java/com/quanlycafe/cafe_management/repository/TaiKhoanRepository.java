package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * TaiKhoanRepository
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 */
@Repository
public interface TaiKhoanRepository extends JpaRepository<TaiKhoan, Integer> {

    /**
     * Tìm tài khoản theo tên đăng nhập
     *
     * @param tenDangNhap String
     * @return Optional<TaiKhoan>
     */
    Optional<TaiKhoan> findByTenDangNhap(String tenDangNhap);

    /**
     *
     * @param tenDangNhap
     * @return
     */
    boolean existsByTenDangNhapIgnoreCase(String tenDangNhap);
}