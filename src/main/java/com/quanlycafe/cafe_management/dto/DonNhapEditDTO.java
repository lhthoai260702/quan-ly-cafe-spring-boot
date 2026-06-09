package com.quanlycafe.cafe_management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DonNhapEditDTO
 * <p>
 * Version 1.1
 * <p>
 * Date: 09-06-2026
 * <p>
 * Copyright
 * <p>
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 09-06-2026   lhthoai     Apply Java Coding Convention
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DonNhapEditDTO {

    @NotNull(message = "Mã đơn nhập không được trống")
    private Integer maDonNhap;

    @NotNull(message = "Ngày nhập không được để trống")
    private LocalDate ngayNhap;

    @NotNull(message = "Số lượng không được để trống")
    private Double soLuong;

    @NotNull(message = "Đơn giá không được để trống")
    private Double donGia;

}