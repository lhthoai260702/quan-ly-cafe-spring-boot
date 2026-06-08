package com.quanlycafe.cafe_management.dto;

import com.quanlycafe.cafe_management.entity.DonViTinh;
import lombok.Data;

import java.math.BigDecimal;

/**
 * InventoryItemDTO
 * Dùng để hiển thị dữ liệu Hàng Hóa kèm theo Tổng Giá Trị tính từ các Đơn Nhập
 */
@Data
public class InventoryItemDTO {
    private Integer maHangHoa;
    private String tenHangHoa;
    private BigDecimal soLuong;
    private BigDecimal donGia;
    private DonViTinh donViTinh;
    private BigDecimal tongGiaTri;
}