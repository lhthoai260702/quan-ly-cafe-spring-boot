package com.quanlycafe.cafe_management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * MenuFormDTO
 * * Version 1.0
 * * Date: 30-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 30-05-2026 lhthoai      Create DTO cho form Thêm/Sửa thực đơn
 */
@Data
public class MenuFormDTO {
    private Integer maThucDon;

    @NotBlank(message = "Tên món không được để trống")
    @Size(max = 100, message = "Tên món không được vượt quá 100 ký tự")
    private String tenMon;

    @NotBlank(message = "Vui lòng chọn phân loại món")
    private String loaiMon;

    @NotNull(message = "Giá tiền không được để trống")
    @Min(value = 0, message = "Giá tiền không được là số âm")
    @Max(value = 99999999, message = "Giá tiền không được vượt quá 99.999.999 VNĐ")
    private Double giaTienHienTai;
}