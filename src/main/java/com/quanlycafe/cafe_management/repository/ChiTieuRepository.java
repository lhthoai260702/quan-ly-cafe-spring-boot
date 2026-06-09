package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.ChiTieu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ChiTieuRepository
 * Version 1.1
 * Date: 09-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 29-05-2026   lhthoai     Create
 * 09-06-2026   lhthoai     Apply Java Coding Convention
 */
@Repository
public interface ChiTieuRepository extends JpaRepository<ChiTieu, Integer> {

    /**
     * Tìm kiếm các khoản chi trong khoảng thời gian xác định
     *
     * @param start LocalDateTime
     * @param end   LocalDateTime
     * @return List<ChiTieu>
     */
    List<ChiTieu> findByNgayChiBetween(LocalDateTime start, LocalDateTime end);

    /**
     * Lấy danh sách khoản chi theo trạng thái cờ xóa
     *
     * @param flagDelete Integer
     * @return List<ChiTieu>
     */
    List<ChiTieu> findByFlagDeleteOrderByNgayChiDesc(Integer flagDelete);

    /**
     * Lấy danh sách khoản chi theo trạng thái cờ xóa có phân trang
     *
     * @param flagDelete Integer
     * @param pageable   Pageable
     * @return Page<ChiTieu>
     */
    Page<ChiTieu> findByFlagDeleteOrderByNgayChiDesc(Integer flagDelete, Pageable pageable);

    /**
     * Tìm kiếm khoản chi theo ID và trạng thái cờ xóa
     *
     * @param id         Integer
     * @param flagDelete Integer
     * @return Optional<ChiTieu>
     */
    Optional<ChiTieu> findByMaChiTieuAndFlagDelete(Integer id, Integer flagDelete);

}