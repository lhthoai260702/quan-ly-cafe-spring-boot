package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.ThucDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ThucDonRepository
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 */
@Repository
public interface ThucDonRepository extends JpaRepository<ThucDon, Integer> {

    /**
     * Lấy danh sách món theo loại
     *
     * @param loaiMon String
     * @return List<ThucDon>
     */
    List<ThucDon> findByLoaiMon(String loaiMon);

    /**
     * Tìm món theo tên
     *
     * @param keyword String
     * @return List<ThucDon>
     */
    List<ThucDon> findByTenMonContainingIgnoreCase(String keyword);

    /**
     * Lấy danh sách các loại món duy nhất
     *
     * @return List<String>
     */
    @Query("SELECT DISTINCT t.loaiMon FROM ThucDon t WHERE t.loaiMon IS NOT NULL")
    List<String> findDistinctLoaiMon();
}