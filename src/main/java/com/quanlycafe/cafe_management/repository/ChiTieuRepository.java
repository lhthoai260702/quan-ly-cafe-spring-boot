package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.ChiTieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ChiTieuRepository
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 */
@Repository
public interface ChiTieuRepository extends JpaRepository<ChiTieu, Integer> {

    /**
     * Tìm danh sách chi tiêu trong khoảng thời gian
     *
     * @param start LocalDateTime
     * @param end   LocalDateTime
     * @return List<ChiTieu>
     */
    List<ChiTieu> findByNgayChiBetween(LocalDateTime start, LocalDateTime end);
}