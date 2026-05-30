package com.quanlycafe.cafe_management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * EquipmentFormDTO
 * * Version 1.0
 * * Date: 30-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 30-05-2026 Quản Lý      Create DTO cho form Thêm/Sửa thiết bị
 */
@Data
public class EquipmentFormDTO {

    private Integer maThietBi;

    @NotBlank(message = "Tên thiết bị không được để trống")
    private String tenThietBi;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng không được nhỏ hơn 0")
    private Integer soLuong;

    private String ghiChu;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayMua;

    @Min(value = 0, message = "Đơn giá mua không được nhỏ hơn 0 VNĐ")
    private Double donGiaMua;
}