package com.quanlycafe.cafe_management.dto;

import com.quanlycafe.cafe_management.validation.ValidPhoneNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
 * 30-05-2026 Quản Lý      Create DTO cho form Thêm/Sửa nhân viên
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

    private String tenDangNhap;
    private String matKhau;
}