package com.quanlycafe.cafe_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ChiTietGoiMonDTO
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChiTietGoiMonDTO {

    private String tenMon;
    private Integer soLuong;
    private Double giaTaiThoiDiemBan;
    private Double thanhTien;

}