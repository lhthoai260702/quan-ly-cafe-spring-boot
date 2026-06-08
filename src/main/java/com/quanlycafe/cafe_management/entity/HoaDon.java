package com.quanlycafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * HoaDon
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 */
@Entity
@Table(name = "hoadon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mahoadon")
    private Integer maHoaDon;

    @Column(name = "tongtien")
    private Double tongTien = 0.0;

    @Column(name = "ngaygiotao", insertable = false, updatable = false)
    private LocalDateTime ngayGioTao;

    @Column(name = "trangthai")
    private String trangThai = "Chưa thanh toán";

    @Column(name = "makhuyenmai")
    private Integer maKhuyenMai;
}