package com.quanlycafe.cafe_management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * DonNhapEditDTO
 * Dùng để hứng dữ liệu chỉnh sửa phiếu nhập
 */
@Data
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