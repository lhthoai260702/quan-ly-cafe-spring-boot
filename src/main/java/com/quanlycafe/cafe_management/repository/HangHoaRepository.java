package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.HangHoa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * HangHoaRepository
 * Version 1.2
 * Date: 30-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 30-05-2026 Quản Lý      Apply Pagination (Pageable)
 */
@Repository
public interface HangHoaRepository extends JpaRepository<HangHoa, Integer> {

    /**
     * Tìm kiếm hàng hóa theo tên (Có phân trang)
     *
     * @param keyword  String
     * @param pageable Pageable
     * @return Page<HangHoa>
     */
    Page<HangHoa> findByTenHangHoaContainingIgnoreCaseOrderByMaHangHoaAsc(String keyword, Pageable pageable);
}