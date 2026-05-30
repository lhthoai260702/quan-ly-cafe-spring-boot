package com.quanlycafe.cafe_management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * PromotionFormDTO
 * * Version 1.0
 * * Date: 30-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 30-05-2026 Quản Lý      Create DTO cho form Thêm/Sửa Khuyến mãi
 */
@Data
public class PromotionFormDTO {

    private Integer maKhuyenMai;

    @NotBlank(message = "Tên chương trình không được để trống")
    private String tenKhuyenMai;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayBatDau;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayKetThuc;

    @NotBlank(message = "Vui lòng chọn loại khuyến mãi")
    private String loaiKhuyenMai;

    @NotNull(message = "Mức giảm không được để trống")
    @Min(value = 1, message = "Mức giảm phải từ 1 trở lên")
    private Double giaTriGiam;

    private String moTa;
}