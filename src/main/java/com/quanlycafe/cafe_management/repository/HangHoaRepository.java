package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.HangHoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * HangHoaRepository
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 */
@Repository
public interface HangHoaRepository extends JpaRepository<HangHoa, Integer> {

    /**
     * Tìm kiếm hàng hóa theo tên
     *
     * @param keyword String
     * @return List<HangHoa>
     */
    List<HangHoa> findByTenHangHoaContainingIgnoreCaseOrderByMaHangHoaAsc(String keyword);
}