package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.KhuyenMai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * KhuyenMaiRepository
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 */
@Repository
public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, Integer> {

    /**
     * Tìm kiếm khuyến mãi theo tên
     *
     * @param keyword String
     * @return List<KhuyenMai>
     */
    List<KhuyenMai> findByTenKhuyenMaiContainingIgnoreCaseOrderByMaKhuyenMaiDesc(String keyword);
}