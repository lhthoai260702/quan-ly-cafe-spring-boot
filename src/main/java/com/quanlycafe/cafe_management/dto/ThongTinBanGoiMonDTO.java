package com.quanlycafe.cafe_management.dto;

import lombok.Data;

import java.util.List;

/**
 * ThongTinBanGoiMonDTO
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 */
@Data
public class ThongTinBanGoiMonDTO {
    private Integer maBan;
    private String tenBan;
    private String tinhTrang;
    private Integer maHoaDon;
    private Double tongTien;
    private List<ChiTietGoiMonDTO> danhSachMon;
}