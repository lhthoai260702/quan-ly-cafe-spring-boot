package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.ThietBi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ThietBiRepository
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 */
@Repository
public interface ThietBiRepository extends JpaRepository<ThietBi, Integer> {

    /**
     * Tìm kiếm thiết bị theo tên
     *
     * @param keyword String
     * @return List<ThietBi>
     */
    List<ThietBi> findByTenThietBiContainingIgnoreCase(String keyword);
}