package com.quanlycafe.cafe_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhoanChiDTO {
    private String tenKhoanChi;
    private BigDecimal soTien;
    private LocalDateTime ngayChi;
}