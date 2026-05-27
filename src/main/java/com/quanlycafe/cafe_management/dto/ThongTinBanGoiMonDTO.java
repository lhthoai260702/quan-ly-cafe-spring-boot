package com.quanlycafe.cafe_management.dto;

import lombok.Data;
import java.util.List;

@Data
public class ThongTinBanGoiMonDTO {
    private Integer maBan;
    private String tenBan;
    private String tinhTrang;
    private Integer maHoaDon;
    private Double tongTien;
    private List<ChiTietGoiMonDTO> danhSachMon;
}