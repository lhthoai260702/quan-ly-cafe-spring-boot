package com.quanlycafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.*;

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