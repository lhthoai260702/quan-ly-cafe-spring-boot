package com.quanlycafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

/**
 * DonViTinh
 * <p>
 * Version 1.0
 * <p>
 * Date: 07-06-2026
 * <p>
 * Copyright
 * <p>
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 07-06-2026 Quản Lý      Create Entity
 */
@Entity
@Table(name = "donvitinh")
@SQLRestriction("flag_delete = 0 OR flag_delete IS NULL")
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

    @Column(name = "flag_delete", columnDefinition = "INT DEFAULT 0")
    private Integer flagDelete = 0;
}