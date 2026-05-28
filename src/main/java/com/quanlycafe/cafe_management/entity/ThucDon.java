package com.quanlycafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(name = "giatienhientai", nullable = false)
    private Double giaTienHienTai;

    @Column(name = "loaimon", length = 50)
    private String loaiMon;
}