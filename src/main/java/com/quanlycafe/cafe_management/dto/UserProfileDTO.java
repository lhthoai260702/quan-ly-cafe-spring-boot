package com.quanlycafe.cafe_management.dto;

import com.quanlycafe.cafe_management.validation.ValidPhoneNumber;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

/**
 * UserProfileDTO
 * Version 1.2
 * Date: 08-06-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 * 30-05-2026 lhthoai       Check validation
 * 08-06-2026 lhthoai       Add fileAnh for Avatar Upload
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

    // Thêm trường này để hứng file ảnh từ form HTML
    private MultipartFile fileAnh;

    private Integer quyenHan;

    @Pattern(regexp = "^$|.{6,}", message = "Mật khẩu mới (nếu có) phải từ 6 ký tự trở lên")
    private String matKhauMoi;

    public String getQuyenHanString() {
        return (quyenHan != null && quyenHan == 1) ? "Quản lý" : "Nhân viên";
    }
}