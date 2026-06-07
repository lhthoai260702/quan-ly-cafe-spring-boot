package com.quanlycafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * ChucVu
 * Version 1.1
 * Date: 07-06-2026
 * Copyright
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 07-06-2026 lthoai      Update mapping DB schema (add flag_delete), clean wildcard imports
 */
@Entity
@Table(name = "chucvu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChucVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "machucvu")
    private Integer maChucVu;

    @Column(name = "tenchucvu", nullable = false, length = 50)
    private String tenChucVu;

    @Column(name = "luong", precision = 12, scale = 2, nullable = false)
    private BigDecimal luong;

    @Column(name = "flag_delete", columnDefinition = "integer default 0")
    private Integer flagDelete;
}