package com.quanlycafe.cafe_management.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * ImportStockDTO
 * Version 1.1
 * Date: 08-06-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 08-06-2026 lhthoai      Rename from StockActionDTO, remove export logic
 */
@Data
public class ImportStockDTO {
    @NotNull(message = "Vui lòng chọn mặt hàng")
    private Integer maHangHoa;

    @NotNull(message = "Số lượng nhập không được để trống")
    @Min(value = 1, message = "Số lượng nhập phải lớn hơn 0")
    @Max(value = 99999999, message = "Số lượng vượt quá giới hạn hệ thống")
    private Double soLuongThaoTac;

    @NotNull(message = "Vui lòng chọn ngày nhập")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayNhap;

    @NotNull(message = "Đơn giá không được để trống")
    @Min(value = 0, message = "Đơn giá không được âm")
    private Double donGia;
}