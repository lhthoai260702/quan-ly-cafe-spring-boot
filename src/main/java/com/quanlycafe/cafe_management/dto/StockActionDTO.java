package com.quanlycafe.cafe_management.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * StockActionDTO
 * * Version 1.0
 * * Date: 30-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 30-05-2026 lhthoai      Create DTO cho form Nhập/Xuất kho
 */
@Data
public class StockActionDTO {
    @NotNull(message = "Vui lòng chọn mặt hàng")
    private Integer maHangHoa;

    @NotNull(message = "Số lượng thao tác không được để trống")
    @Min(value = 1, message = "Số lượng thao tác phải lớn hơn 0")
    @Max(value = 99999999, message = "Số lượng vượt quá giới hạn hệ thống")
    private Double soLuongThaoTac;
}