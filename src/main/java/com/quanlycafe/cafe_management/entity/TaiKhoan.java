package com.quanlycafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "taikhoan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaiKhoan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mataikhoan")
    private Integer maTaiKhoan;

    @Column(name = "tendangnhap", nullable = false, unique = true, length = 50)
    private String tenDangNhap;

    @Column(name = "matkhau", nullable = false, length = 255)
    private String matKhau;

    @Column(name = "quyenhan", nullable = false)
    private Integer quyenHan; // 1: Quản lý, 2: Nhân viên

    @Column(name = "anh", columnDefinition = "TEXT")
    private String anh;
}
