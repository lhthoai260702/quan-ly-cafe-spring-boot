package com.quanlycafe.cafe_management.dto;

import lombok.Data;
import java.math.BigDecimal;

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

    public String getQuyenHanString() {
        return (quyenHan != null && quyenHan == 1) ? "Quản lý" : "Nhân viên";
    }
}