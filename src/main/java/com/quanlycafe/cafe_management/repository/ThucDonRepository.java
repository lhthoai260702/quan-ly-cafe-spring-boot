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
 * <p>
 * Version 1.1
 * <p>
 * Date: 30-05-2026
 * <p>
 * Copyright
 * <p>
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai      Create
 * 30-05-2026 lhthoai      Apply pagination (Pageable) and Convention
 */
@Repository
public interface ThucDonRepository extends JpaRepository<ThucDon, Integer> {

    /**
     * Lấy danh sách món theo loại (Có phân trang)
     *
     * @param loaiMon
     * @param pageable
     * @return Page<ThucDon>
     */
    Page<ThucDon> findByLoaiMon(String loaiMon, Pageable pageable);

    /**
     * Tìm món theo tên (Có phân trang)
     *
     * @param keyword
     * @param pageable
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