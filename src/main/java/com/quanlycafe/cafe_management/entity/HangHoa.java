package com.quanlycafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "hanghoa")
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "madonvitinh")
    private DonViTinh donViTinh;

    @Column(name = "dongia", nullable = false, precision = 12, scale = 2)
    private BigDecimal donGia;
}