package com.quanlycafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
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