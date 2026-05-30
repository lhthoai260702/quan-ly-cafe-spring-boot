package com.quanlycafe.cafe_management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * InventoryFormDTO
 * * Version 1.0
 * * Date: 30-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 30-05-2026 Quản Lý      Create DTO cho form Thêm/Sửa Hàng hóa
 */
@Data
public class InventoryFormDTO {

    private Integer maHangHoa;

    @NotBlank(message = "Tên mặt hàng không được để trống")
    private String tenHangHoa;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng không được âm")
    private Double soLuong;

    @NotNull(message = "Vui lòng chọn đơn vị tính")
    private Integer maDonViTinh;

    @NotNull(message = "Đơn giá không được để trống")
    @Min(value = 0, message = "Đơn giá không được âm")
    private Double donGia;
}