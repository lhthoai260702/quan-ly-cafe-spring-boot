package com.quanlycafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * ChiTietThucDon
 * <p>
 * Version 1.1
 * <p>
 * Date: 07-06-2026
 * <p>
 * Copyright
 * <p>
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 07-06-2026 lhthoai      Create Entity (Sử dụng separate ChiTietThucDonId)
 */
@Entity
@Table(name = "chitietthucdon")
@IdClass(ChiTietThucDonId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietThucDon {

    @Id
    @Column(name = "mahanghoa")
    private Integer maHangHoa;

    @Id
    @Column(name = "mathucdon")
    private Integer maThucDon;

    @Column(name = "khoiluong", nullable = false, precision = 10, scale = 2)
    private BigDecimal khoiLuong;

    @Column(name = "donvitinh", length = 50)
    private String donViTinh;
}