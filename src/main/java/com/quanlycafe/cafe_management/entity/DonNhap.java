package com.quanlycafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DonNhap
 * Version 1.0
 * Date: 08-06-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 08-06-2026 lhthoai      Create Entity based on DB schema
 */
@Entity
@Table(name = "donnhap")
@SQLRestriction("flag_delete = 0 OR flag_delete IS NULL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonNhap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "madonnhap")
    private Integer maDonNhap;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manhanvien")
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mathietbi")
    private ThietBi thietBi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mahanghoa")
    private HangHoa hangHoa;

    @Column(name = "ngaynhap")
    private LocalDateTime ngayNhap;

    @Column(name = "tongtien", nullable = false, precision = 12, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "soluong", nullable = false, precision = 10, scale = 2)
    private BigDecimal soLuong;

    @Column(name = "flag_delete", columnDefinition = "INT DEFAULT 0")
    private Integer flagDelete = 0;
}