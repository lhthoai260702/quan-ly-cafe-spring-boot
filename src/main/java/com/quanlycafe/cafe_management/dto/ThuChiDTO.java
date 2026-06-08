package com.quanlycafe.cafe_management.dto;

import com.quanlycafe.cafe_management.entity.HoaDon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThuChiDTO {
    private LocalDate ngay;
    private BigDecimal thu;
    private BigDecimal chi;

    private List<HoaDon> danhSachThu = new ArrayList<>();
    private List<KhoanChiDTO> danhSachChi = new ArrayList<>(); // Đã thay đổi

    public ThuChiDTO(LocalDate ngay, BigDecimal thu, BigDecimal chi) {
        this.ngay = ngay;
        this.thu = thu;
        this.chi = chi;
    }
}