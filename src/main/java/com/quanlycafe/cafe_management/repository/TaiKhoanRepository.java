package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.TaiKhoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * TaiKhoanRepository
 * Version 1.1
 * Date: 09-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 29-05-2026   lhthoai     Create
 * 09-06-2026   lhthoai     Apply Java Coding Convention
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
     * Kiểm tra sự tồn tại của tên đăng nhập (không phân biệt hoa thường)
     *
     * @param tenDangNhap String
     * @return boolean
     */
    boolean existsByTenDangNhapIgnoreCase(String tenDangNhap);

}