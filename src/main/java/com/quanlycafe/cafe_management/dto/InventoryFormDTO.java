package com.quanlycafe.cafe_management.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * InventoryFormDTO
 * * Version 1.0
 * * Date: 30-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 30-05-2026 lhthoai      Create DTO cho form Thêm/Sửa Hàng hóa
 */
@Data
public class InventoryFormDTO {
    private Integer maHangHoa;

    @NotBlank(message = "Tên hàng hóa không được để trống")
    @Size(max = 100, message = "Tên hàng hóa không được vượt quá 100 ký tự")
    private String tenHangHoa;

    @NotNull(message = "Vui lòng chọn đơn vị tính")
    private Integer maDonViTinh;

    @Min(value = 0, message = "Số lượng không được là số âm")
    @Max(value = 99999999, message = "Số lượng vượt quá giới hạn hệ thống")
    private Double soLuong;

    @NotNull(message = "Đơn giá không được để trống")
    private Double donGia;

    @NotNull(message = "Vui lòng chọn ngày nhập")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayNhap;
}