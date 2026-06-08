package com.quanlycafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ChiTietDatBan
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 */
@Entity
@Table(name = "chitietdatban")
@IdClass(ChiTietDatBanId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDatBan {

    @Id
    @Column(name = "maban")
    private Integer maBan;

    @Id
    @Column(name = "manhanvien")
    private Integer maNhanVien;

    @Id
    @Column(name = "mahoadon")
    private Integer maHoaDon;

    @Column(name = "tenkhachhang", nullable = false)
    private String tenKhachHang;

    @Column(name = "sdtkhachhang")
    private String sdtKhachHang;

    @Column(name = "ngaygiodat", nullable = false)
    private LocalDateTime ngayGioDat;
}