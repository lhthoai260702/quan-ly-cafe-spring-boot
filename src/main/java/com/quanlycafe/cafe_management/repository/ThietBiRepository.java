package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.ThietBi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ThietBiRepository
 * Version 1.3
 * Date: 07-06-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 * 06-06-2026 lhthoai      Add Pagination
 * 07-06-2026 lhthoai      Add filters for TinhTrang and flagDelete
 */
@Repository
public interface ThietBiRepository extends JpaRepository<ThietBi, Integer> {

    /**
     * Tìm kiếm thiết bị theo tên (bỏ qua thiết bị đã xóa mềm)
     *
     * @param keyword    String
     * @param flagDelete Integer
     * @param pageable   Pageable
     * @return Page<ThietBi>
     */
    Page<ThietBi> findByTenThietBiContainingIgnoreCaseAndFlagDelete(String keyword, Integer flagDelete, Pageable pageable);

    /**
     * Tìm kiếm thiết bị theo tên và tình trạng (bỏ qua thiết bị đã xóa mềm)
     *
     * @param keyword    String
     * @param tinhTrang  String
     * @param flagDelete Integer
     * @param pageable   Pageable
     * @return Page<ThietBi>
     */
    Page<ThietBi> findByTenThietBiContainingIgnoreCaseAndTinhTrangAndFlagDelete(String keyword, String tinhTrang, Integer flagDelete, Pageable pageable);
}