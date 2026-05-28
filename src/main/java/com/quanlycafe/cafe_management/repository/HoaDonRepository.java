package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    List<HoaDon> findByNgayGioTaoBetweenAndTrangThai(LocalDateTime start, LocalDateTime end, String trangThai);

    @org.springframework.data.jpa.repository.Query(value =
            "SELECT t.tenmon, SUM(c.soluong) as tong_so_luong " +
                    "FROM chitiethoadon c " +
                    "JOIN thucdon t ON c.mathucdon = t.mathucdon " +
                    "JOIN hoadon h ON c.mahoadon = h.mahoadon " +
                    "WHERE h.trangthai = 'Đã thanh toán' " +
                    "GROUP BY t.tenmon " +
                    "ORDER BY tong_so_luong DESC LIMIT 5", nativeQuery = true)
    java.util.List<Object[]> getTopSellingDishes();
}