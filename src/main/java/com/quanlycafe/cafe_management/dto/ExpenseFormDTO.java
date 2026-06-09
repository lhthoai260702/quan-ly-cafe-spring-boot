package com.quanlycafe.cafe_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * ExpenseFormDTO
 * Version 1.1
 * Date: 09-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 09-06-2026   lhthoai     Apply Java Coding Convention
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseFormDTO {

    private Integer id;

    @NotBlank(message = "Vui lòng nhập tên khoản chi")
    private String tenKhoanChi;

    @NotNull(message = "Vui lòng nhập số tiền")
    private Double soTien;

    @NotNull(message = "Vui lòng chọn ngày chi")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayChi;

}