package com.quanlycafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "chitieu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTieu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "machitieu")
    private Integer maChiTieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mataikhoan")
    private TaiKhoan taiKhoan;

    @Column(name = "sotien", nullable = false, precision = 12, scale = 2)
    private BigDecimal soTien;

    @Column(name = "tenkhoanchi", nullable = false, length = 100)
    private String tenKhoanChi;

    @Column(name = "ngaychi")
    private LocalDateTime ngayChi;

    @Column(name = "flag_delete")
    private Integer flagDelete = 0;
}