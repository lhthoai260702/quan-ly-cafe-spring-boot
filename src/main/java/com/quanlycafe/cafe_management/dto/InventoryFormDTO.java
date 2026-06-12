package com.quanlycafe.cafe_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * InventoryFormDTO
 * Version 2.0
 * Date: 12-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 30-05-2026   lhthoai     Create DTO cho form Thêm/Sửa Hàng hóa
 * 12-06-2026   Quản Lý     Gộp DTO, bổ sung đơn vị sử dụng và làm DTO đa năng
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryFormDTO {

    private Integer id; // Dùng chung: Lúc là mã hàng hóa, lúc là mã đơn nhập

    private String tenHangHoa;
    private Integer maDonViTinh;
    private Integer maDonViSuDung;
    private Double soLuong;
    private Double donGia;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayNhap;
}