package com.quanlycafe.cafe_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * KhoanChiDTO
 * Version 1.1
 * Date: 09-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 09-06-2026   lhthoai     Apply Java Coding Convention
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class KhoanChiDTO {

    private String tenKhoanChi;
    private BigDecimal soTien;
    private LocalDateTime ngayChi;

}