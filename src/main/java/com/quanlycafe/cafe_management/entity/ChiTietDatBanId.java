package com.quanlycafe.cafe_management.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * ChiTietDatBanId
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDatBanId implements Serializable {
    private Integer maBan;
    private Integer maNhanVien;
    private Integer maHoaDon;
}