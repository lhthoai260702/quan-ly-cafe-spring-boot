package com.quanlycafe.cafe_management.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "thietbi")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThietBi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mathietbi")
    private Integer maThietBi;

    @Column(name = "tenthietbi", nullable = false, length = 100)
    private String tenThietBi;

    @Column(name = "soluong", nullable = false)
    private Integer soLuong = 0;

    @Column(name = "ghichu", columnDefinition = "TEXT")
    private String ghiChu;

    @Column(name = "ngaymua")
    private LocalDate ngayMua;

    @Column(name = "dongiamua", precision = 12, scale = 2)
    private BigDecimal donGiaMua;
}