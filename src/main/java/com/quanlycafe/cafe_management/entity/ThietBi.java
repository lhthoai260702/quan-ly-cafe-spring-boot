package com.quanlycafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ThietBi
 * Version 1.1
 * Date: 07-06-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 07-06-2026 lthoai       Update entity to match new database schema
 */
@Entity
@Table(name = "thietbi")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThietBi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mathietbi")
    private Integer maThietBi;

    @Column(name = "tenthietbi", nullable = false, length = 100)
    private String tenThietBi;

    @Column(name = "tinhtrang", length = 50)
    private String tinhTrang = "Hoạt động tốt";

    @Column(name = "ghichu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "ngaymua", nullable = false)
    private LocalDate ngayMua;

    @Column(name = "dongiamua", precision = 12, scale = 2)
    private BigDecimal donGiaMua;

    @Column(name = "flag_delete")
    private Integer flagDelete = 0;
}