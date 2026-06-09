package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.DonNhap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DonNhapRepository
 * Version 1.1
 * Date: 09-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 08-06-2026   lhthoai     Rename from StockActionDTO, remove export logic
 * 09-06-2026   Quản Lý     Apply Java Coding Convention
 */
@Repository
public interface DonNhapRepository extends JpaRepository<DonNhap, Integer> {

    /**
     * Tìm kiếm danh sách đơn nhập theo mã hàng hóa và trạng thái xóa
     *
     * @param maHangHoa  Integer
     * @param flagDelete Integer
     * @return List<DonNhap>
     */
    List<DonNhap> findByHangHoa_MaHangHoaAndFlagDeleteOrderByNgayNhapDesc(Integer maHangHoa, Integer flagDelete);

    /**
     * Tìm kiếm đơn nhập trong khoảng thời gian xác định
     *
     * @param start LocalDateTime
     * @param end   LocalDateTime
     * @return List<DonNhap>
     */
    List<DonNhap> findByNgayNhapBetween(LocalDateTime start, LocalDateTime end);

}