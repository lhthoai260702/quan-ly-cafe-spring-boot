package com.quanlycafe.cafe_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * ThongTinBanGoiMonDTO
 * Version 1.1
 * Date: 09-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 29-05-2026   lhthoai     Create
 * 09-06-2026   lhthoai     Apply Java Coding Convention
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThongTinBanGoiMonDTO {

    private Integer maBan;
    private String tenBan;
    private String tinhTrang;
    private Integer maHoaDon;
    private Double tongTien;
    private List<ChiTietGoiMonDTO> danhSachMon;

}