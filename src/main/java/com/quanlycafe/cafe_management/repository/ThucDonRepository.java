package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.ThucDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ThucDonRepository
 * Version 1.1
 * Date: 30-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 30-05-2026 Quản Lý      Apply pagination (Pageable)
 */
@Repository
public interface ThucDonRepository extends JpaRepository<ThucDon, Integer> {

    /**
     * Lấy danh sách món theo loại (Có phân trang)
     *
     * @param loaiMon  String
     * @param pageable Pageable
     * @return Page<ThucDon>
     */
    Page<ThucDon> findByLoaiMon(String loaiMon, Pageable pageable);

    /**
     * Tìm món theo tên (Có phân trang)
     *
     * @param keyword  String
     * @param pageable Pageable
     * @return Page<ThucDon>
     */
    Page<ThucDon> findByTenMonContainingIgnoreCase(String keyword, Pageable pageable);

    /**
     * Lấy danh sách các loại món duy nhất
     *
     * @return List<String>
     */
    @Query("SELECT DISTINCT t.loaiMon FROM ThucDon t WHERE t.loaiMon IS NOT NULL")
    List<String> findDistinctLoaiMon();
}