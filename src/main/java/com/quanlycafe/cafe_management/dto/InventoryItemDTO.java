package com.quanlycafe.cafe_management.dto;

import com.quanlycafe.cafe_management.entity.DonViTinh;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * InventoryItemDTO
 * Version 1.1
 * Date: 09-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 30-05-2026   lhthoai     Create
 * 09-06-2026   lhthoai     Apply Java Coding Convention
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
    private BigDecimal tongGiaTri;

}