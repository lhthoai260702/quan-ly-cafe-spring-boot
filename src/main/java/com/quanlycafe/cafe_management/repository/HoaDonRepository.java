package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * HoaDonRepository
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 */
@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {

    /**
     * Tìm hóa đơn theo khoảng thời gian và trạng thái
     *
     * @param start     LocalDateTime
     * @param end       LocalDateTime
     * @param trangThai String
     * @return List<HoaDon>
     */
    List<HoaDon> findByNgayGioTaoBetweenAndTrangThai(LocalDateTime start, LocalDateTime end, String trangThai);

    /**
     * Thống kê 5 món bán chạy nhất trong khoảng thời gian (Lọc bỏ hóa đơn đã hủy)
     *
     * @return List<Object[]>
     */
    @Query(value =
            "SELECT t.tenmon, SUM(c.soluong) as tong_so_luong " +
                    "FROM chitiethoadon c " +
                    "JOIN thucdon t ON c.mathucdon = t.mathucdon " +
                    "JOIN hoadon h ON c.mahoadon = h.mahoadon " +
                    "WHERE h.trangthai = 'Đã thanh toán' " +
                    "AND (h.flag_delete = 0 OR h.flag_delete IS NULL) " +
                    "AND h.ngaygiotao >= :startDate AND h.ngaygiotao <= :endDate " +
                    "GROUP BY t.tenmon " +
                    "ORDER BY tong_so_luong DESC LIMIT 5", nativeQuery = true)
    List<Object[]> getTopSellingDishesCurrentMonth(@org.springframework.data.repository.query.Param("startDate") java.time.LocalDateTime startDate,
                                                   @org.springframework.data.repository.query.Param("endDate") java.time.LocalDateTime endDate);
}