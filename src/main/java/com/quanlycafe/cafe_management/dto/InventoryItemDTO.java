package com.quanlycafe.cafe_management.dto;

import com.quanlycafe.cafe_management.entity.DonViTinh;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * InventoryItemDTO
 * Version 1.2
 * Date: 12-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 30-05-2026   lhthoai     Create
 * 12-06-2026   Quản Lý     Thêm DonViSuDung
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItemDTO {

    private Integer maHangHoa;
    private String tenHangHoa;
    private BigDecimal soLuong;
    private BigDecimal donGia;
    private DonViTinh donViTinh;
    private DonViTinh donViSuDung; // Cột mới hiển thị
    private BigDecimal tongGiaTri;

}