package com.quanlycafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;

/**
 * HangHoa
 * Version 1.1
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 * 07-06-2026 lhthoai       Update DB schema (Add flag_delete, SQLRestriction)
 * 12-06-2026 lhthoai      Bổ sung trường donViSuDung theo schema mới
 */
@Entity
@Table(name = "hanghoa")
@SQLRestriction("flag_delete = 0 OR flag_delete IS NULL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HangHoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mahanghoa")
    private Integer maHangHoa;

    @Column(name = "tenhanghoa", nullable = false, length = 100)
    private String tenHangHoa;

    @Column(name = "soluong")
    private BigDecimal soLuong;

    // Đơn vị tính cơ bản (nhập kho)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "madonvitinh")
    private DonViTinh donViTinh;

    // Đơn vị dùng để quy đổi khi sử dụng/chế biến
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "madonvisudung")
    private DonViTinh donViSuDung;

    @Column(name = "dongia", nullable = false, precision = 12, scale = 2)
    private BigDecimal donGia;

    @Column(name = "flag_delete", columnDefinition = "INT DEFAULT 0")
    private Integer flagDelete = 0;
}