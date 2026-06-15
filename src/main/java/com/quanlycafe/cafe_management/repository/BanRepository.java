package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.Ban;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * BanRepository
 * Version 1.2
 * Date: 13-06-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai      Create
 * 30-05-2026 lhthoai      Add Pagination and Count queries
 * 13-06-2026 Quản Lý      Add custom Query to sort table name ignore case
 */
@Repository
public interface BanRepository extends JpaRepository<Ban, Integer> {

    /**
     * Tìm bàn theo tên (có phân trang)
     *
     * @param tenBan   String
     * @param pageable Pageable
     * @return Page<Ban>
     */
    Page<Ban> findByTenBanContainingIgnoreCase(String tenBan, Pageable pageable);

    /**
     * Tìm bàn theo tình trạng và tên (có phân trang)
     *
     * @param tinhTrang String
     * @param tenBan    String
     * @param pageable  Pageable
     * @return Page<Ban>
     */
    Page<Ban> findByTinhTrangAndTenBanContainingIgnoreCase(String tinhTrang, String tenBan, Pageable pageable);

    /**
     * Đếm số lượng bàn theo tình trạng
     *
     * @param tinhTrang String
     * @return long
     */
    long countByTinhTrang(String tinhTrang);

    /**
     * Lấy danh sách bàn theo tình trạng (Dùng cho dropdown Modal)
     * Sắp xếp theo tên bàn tăng dần (Không phân biệt hoa thường)
     *
     * @param tinhTrang String
     * @return List<Ban>
     */
    @Query("SELECT b FROM Ban b WHERE b.tinhTrang = :tinhTrang ORDER BY LOWER(b.tenBan) ASC")
    List<Ban> findByTinhTrang(@Param("tinhTrang") String tinhTrang);
}