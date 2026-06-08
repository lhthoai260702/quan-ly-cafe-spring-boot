package com.quanlycafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * KhuyenMai
 * <p>
 * Version 1.1
 * <p>
 * Date: 08-06-2026
 * <p>
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 * 08-06-2026 lhthoai      Add flag_delete for soft delete functionality
 */
@Entity
@Table(name = "khuyenmai")
@SQLRestriction("flag_delete = 0 OR flag_delete IS NULL")
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

    @Column(name = "flag_delete")
    private Integer flagDelete;
}