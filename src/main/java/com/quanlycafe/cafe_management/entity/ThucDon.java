package com.quanlycafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * ThucDon
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 */
@Entity
@Table(name = "thucdon")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThucDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mathucdon")
    private Integer maThucDon;

    @Column(name = "tenmon", nullable = false, length = 100)
    private String tenMon;

    @Column(name = "giatienhientai", nullable = false, precision = 12, scale = 2)
    private BigDecimal giaTienHienTai;

    @Column(name = "loaimon", length = 50)
    private String loaiMon;
}