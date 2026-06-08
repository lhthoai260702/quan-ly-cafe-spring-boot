package com.quanlycafe.cafe_management.dto;

import com.quanlycafe.cafe_management.validation.ValidPhoneNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * EmployeeFormDTO
 * * Version 1.0
 * * Date: 30-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 30-05-2026 lhthoai      Create DTO cho form Thêm/Sửa nhân viên
 */
@Data
public class EmployeeFormDTO {

    private Integer maNhanVien;

    @NotBlank(message = "Họ và tên không được để trống")
    @Size(min = 2, max = 50, message = "Họ tên phải từ 2 đến 50 ký tự")
    private String hoTen;

    @ValidPhoneNumber
    private String soDienThoai;

    @NotBlank(message = "Địa chỉ không được để trống")
    private String diaChi;

    @NotNull(message = "Vui lòng chọn chức vụ")
    private Integer maChucVu;

    @NotBlank(message = "Tên đăng nhập không được để trống")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Tên đăng nhập viết liền không dấu, không khoảng trắng")
    private String tenDangNhap;

    @NotBlank(message = "Mật khẩu không được để trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    @Pattern(regexp = "^[\\x20-\\x7E]+$", message = "Mật khẩu không được chứa ký tự tiếng Việt có dấu")
    private String matKhau;

    private java.math.BigDecimal luong;
}