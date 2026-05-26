package com.quanlycafe.cafe_management.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UserProfileDTO {
    private String hoTen;
    private String soDienThoai;
    private BigDecimal luong;
    private String tenChucVu;
    private String tenDangNhap;
    private String anh;
    private Integer quyenHan; // 1: Quản lý, 2: Nhân viên

    // Thuộc tính tiện ích để hiển thị chuỗi quyền hạn trên Header
    public String getQuyenHanString() {
        return (quyenHan != null && quyenHan == 1) ? "Quản lý" : "Nhân viên";
    }
}