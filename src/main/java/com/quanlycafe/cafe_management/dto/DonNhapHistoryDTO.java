package com.quanlycafe.cafe_management.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DonNhapHistoryDTO
 * Dùng để trả dữ liệu lịch sử nhập hàng qua API
 */
@Data
public class DonNhapHistoryDTO {
    private Integer maDonNhap;
    private LocalDateTime ngayNhap;
    private BigDecimal soLuong;
    private BigDecimal donGia;
    private BigDecimal tongTien;
}