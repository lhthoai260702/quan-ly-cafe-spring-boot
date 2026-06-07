package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.ChiTietThucDon;
import com.quanlycafe.cafe_management.entity.ChiTietThucDonId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * ChiTietThucDonRepository
 * <p>
 * Version 1.1
 * <p>
 * Date: 07-06-2026
 * <p>
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 07-06-2026 Quản Lý      Create Repository
 * 07-06-2026 Quản Lý      Fix Bug: Add @Modifying and @Transactional for delete method
 */
@Repository
public interface ChiTietThucDonRepository extends JpaRepository<ChiTietThucDon, ChiTietThucDonId> {

    /**
     * Xóa tất cả các thành phần của một món (Dùng khi cập nhật món)
     *
     * @param maThucDon Integer
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM ChiTietThucDon c WHERE c.maThucDon = :maThucDon")
    void deleteByMaThucDon(@Param("maThucDon") Integer maThucDon);

    /**
     * Tìm toàn bộ nguyên liệu của 1 món ăn
     *
     * @param maThucDon Integer
     * @return List<ChiTietThucDon>
     */
    List<ChiTietThucDon> findByMaThucDon(Integer maThucDon);
}