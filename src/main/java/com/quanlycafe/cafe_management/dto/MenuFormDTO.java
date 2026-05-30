package com.quanlycafe.cafe_management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * MenuFormDTO
 * * Version 1.0
 * * Date: 30-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 30-05-2026 Quản Lý      Create DTO cho form Thêm/Sửa thực đơn
 */
@Data
public class MenuFormDTO {

    private Integer maThucDon;

    @NotBlank(message = "Tên món không được để trống")
    private String tenMon;

    @NotNull(message = "Giá tiền không được để trống")
    @Min(value = 0, message = "Giá tiền phải lớn hơn hoặc bằng 0 VNĐ")
    private Double giaTienHienTai;

    @NotBlank(message = "Loại món không được để trống")
    private String loaiMon;
}