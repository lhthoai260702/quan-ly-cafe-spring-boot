package com.quanlycafe.cafe_management.dto;

import com.quanlycafe.cafe_management.entity.ChiTieu;
import com.quanlycafe.cafe_management.entity.HoaDon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ThuChiDTO
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ThuChiDTO {
    private LocalDate ngay;
    private BigDecimal thu;
    private BigDecimal chi;

    private List<HoaDon> danhSachThu = new ArrayList<>();
    private List<ChiTieu> danhSachChi = new ArrayList<>();

    /**
     * Khởi tạo ThuChiDTO
     *
     * @param ngay LocalDate
     * @param thu  BigDecimal
     * @param chi  BigDecimal
     */
    public ThuChiDTO(LocalDate ngay, BigDecimal thu, BigDecimal chi) {
        this.ngay = ngay;
        this.thu = thu;
        this.chi = chi;
    }
}