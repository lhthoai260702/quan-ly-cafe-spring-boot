package com.quanlycafe.cafe_management.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * PromotionFormDTO
 * * Version 1.0
 * * Date: 30-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 30-05-2026 lhthoai      Create DTO cho form Thêm/Sửa Khuyến mãi
 * 03-06-2026 lhthoai      chỉnh sửa
 */
@Data
public class PromotionFormDTO {
    private Integer maKhuyenMai;

    @NotBlank(message = "Tên khuyến mãi không được để trống")
    @Size(max = 100, message = "Tên khuyến mãi không được vượt quá 100 ký tự")
    private String tenKhuyenMai;

    @NotNull(message = "Vui lòng chọn ngày bắt đầu")
    private LocalDate ngayBatDau;

    @NotNull(message = "Vui lòng chọn ngày kết thúc")
    private LocalDate ngayKetThuc;

    @NotBlank(message = "Vui lòng chọn loại khuyến mãi")
    private String loaiKhuyenMai;

    @NotNull(message = "Giá trị giảm không được để trống")
    @Min(value = 0, message = "Giá trị giảm không được là số âm")
    @Max(value = 99999999, message = "Mức giảm không được vượt quá 99.999.999")
    private Double giaTriGiam;

    private String moTa;
}