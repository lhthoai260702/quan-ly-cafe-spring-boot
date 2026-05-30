package com.quanlycafe.cafe_management.dto;

import com.quanlycafe.cafe_management.validation.ValidPhoneNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

/**
 * UserProfileDTO
 * Version 1.1
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 30-05-2026 lthoai       Check validation
 */
@Data
public class UserProfileDTO {
    private Integer maNhanVien;
    private String diaChi;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 50, message = "Họ tên phải từ 2 đến 50 ký tự")
    private String hoTen;

    @ValidPhoneNumber
    private String soDienThoai;

    private BigDecimal luong;
    private String tenChucVu;
    private Integer maChucVu;
    private String tenDangNhap;
    private String anh;
    private Integer quyenHan;

    @Size(min = 6, message = "Mật khẩu mới (nếu có) phải từ 6 ký tự trở lên")
    private String matKhauMoi;

    public String getQuyenHanString() {
        return (quyenHan != null && quyenHan == 1) ? "Quản lý" : "Nhân viên";
    }
}