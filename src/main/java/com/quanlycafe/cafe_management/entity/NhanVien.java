package com.quanlycafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nhanvien")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhanVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manhanvien")
    private Integer maNhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "machucvu")
    private ChucVu chucVu;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mataikhoan")
    private TaiKhoan taiKhoan;

    @Column(name = "hoten", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "sodienthoai", length = 15)
    private String soDienThoai;

    @Column(name = "diachi", length = 255)
    private String diaChi;
}
