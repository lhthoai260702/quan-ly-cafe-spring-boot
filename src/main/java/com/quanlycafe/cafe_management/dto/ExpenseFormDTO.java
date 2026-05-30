package com.quanlycafe.cafe_management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * ExpenseFormDTO
 * * Version 1.0
 * * Date: 30-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 30-05-2026 Quản Lý      Create DTO cho form Thêm khoản chi
 */
@Data
public class ExpenseFormDTO {

    @NotBlank(message = "Mục đích chi không được để trống")
    private String tenKhoanChi;

    @NotNull(message = "Số tiền chi không được để trống")
    @Min(value = 1000, message = "Số tiền chi phải từ 1,000 VNĐ trở lên")
    private Double soTien;

    @NotNull(message = "Ngày chi không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayChi;
}