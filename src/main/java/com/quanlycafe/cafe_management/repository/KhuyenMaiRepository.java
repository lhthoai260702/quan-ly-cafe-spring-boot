package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.KhuyenMai;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * KhuyenMaiRepository
 * Version 1.3
 * Date: 08-06-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 * 30-05-2026 lhthoai      Apply Pagination (Pageable)
 * 08-06-2026 lhthoai      Support soft delete natively
 */
@Repository
public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, Integer> {

    /**
     * Tìm kiếm khuyến mãi theo tên (Có phân trang)
     *
     * @param keyword  String
     * @param pageable Pageable
     * @return Page<KhuyenMai>
     */
    Page<KhuyenMai> findByTenKhuyenMaiContainingIgnoreCaseOrderByMaKhuyenMaiDesc(String keyword, Pageable pageable);
}