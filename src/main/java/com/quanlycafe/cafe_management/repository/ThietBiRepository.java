package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.ThietBi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ThietBiRepository
 * Version 1.2
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 06-06-2026 Quản Lý      Add Pagination
 */
@Repository
public interface ThietBiRepository extends JpaRepository<ThietBi, Integer> {

    /**
     * Tìm kiếm thiết bị theo tên có phân trang
     *
     * @param keyword  String
     * @param pageable Pageable
     * @return Page<ThietBi>
     */
    Page<ThietBi> findByTenThietBiContainingIgnoreCase(String keyword, Pageable pageable);
}