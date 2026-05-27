package com.quanlycafe.cafe_management.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class UserProfileDTO {
    // Thêm 2 trường này phục vụ cho trang Quản lý nhân viên (Sửa, Xóa, Hiển thị)
    private Integer maNhanVien;
    private String diaChi;

    // Các trường cũ giữ nguyên
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