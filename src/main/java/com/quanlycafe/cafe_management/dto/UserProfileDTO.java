package com.quanlycafe.cafe_management.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * UserProfileDTO
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 */
@Data
public class UserProfileDTO {
    private Integer maNhanVien;
    private String diaChi;
    private String hoTen;
    private String soDienThoai;
    private BigDecimal luong;
    private String tenChucVu;
    private Integer maChucVu;
    private String tenDangNhap;
    private String anh;
    private Integer quyenHan; // 1: Quản lý, 2: Nhân viên

    /**
     * Lấy tên quyền hạn dưới dạng chuỗi
     *
     * @return String
     */
    public String getQuyenHanString() {
        return (quyenHan != null && quyenHan == 1) ? "Quản lý" : "Nhân viên";
    }
}