package com.quanlycafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

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
}