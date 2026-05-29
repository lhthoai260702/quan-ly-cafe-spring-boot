package com.quanlycafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DonViTinh
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 */
@Entity
@Table(name = "donvitinh")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DonViTinh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "madonvitinh")
    private Integer maDonViTinh;

    @Column(name = "tendonvi", nullable = false, length = 50)
    private String tenDonVi;
}