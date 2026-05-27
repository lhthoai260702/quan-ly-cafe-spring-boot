package com.quanlycafe.cafe_management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChiTietGoiMonDTO {
    private String tenMon;
    private Integer soLuong;
    private Double giaTaiThoiDiemBan;
    private Double thanhTien;
}