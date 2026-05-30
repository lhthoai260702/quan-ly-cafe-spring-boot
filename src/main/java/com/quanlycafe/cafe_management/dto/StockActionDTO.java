package com.quanlycafe.cafe_management.dto;

import jakarta.validation.constraints.DecimalMin;
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
 * 30-05-2026 Quản Lý      Create DTO cho form Nhập/Xuất kho
 */
@Data
public class StockActionDTO {

    @NotNull(message = "Mã hàng hóa bị thiếu")
    private Integer maHangHoa;

    @NotNull(message = "Vui lòng nhập số lượng")
    @DecimalMin(value = "0.1", message = "Số lượng phải lớn hơn 0")
    private Double soLuongThaoTac; // Dùng chung cho cả biến soLuongNhap và soLuongXuat
}