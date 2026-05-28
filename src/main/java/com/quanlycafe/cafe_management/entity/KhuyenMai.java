package com.quanlycafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "khuyenmai")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhuyenMai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "makhuyenmai")
    private Integer maKhuyenMai;

    @Column(name = "tenkhuyenmai", nullable = false, length = 100)
    private String tenKhuyenMai;

    @Column(name = "ngaybatdau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "ngayketthuc", nullable = false)
    private LocalDate ngayKetThuc;

    @Column(name = "loaikhuyenmai", length = 50)
    private String loaiKhuyenMai;

    @Column(name = "giatrigiam", nullable = false, precision = 10, scale = 2)
    private BigDecimal giaTriGiam;

    @Column(name = "trangthai", length = 50)
    private String trangThai;

    @Column(name = "mota", columnDefinition = "TEXT")
    private String moTa;
}