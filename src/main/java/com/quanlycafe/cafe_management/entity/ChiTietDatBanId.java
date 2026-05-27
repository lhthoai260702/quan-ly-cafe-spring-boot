package com.quanlycafe.cafe_management.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDatBanId implements Serializable {
    private Integer maBan;
    private Integer maNhanVien;
    private Integer maHoaDon;
}