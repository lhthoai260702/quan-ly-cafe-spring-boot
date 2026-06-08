package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.ChiTieu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ChiTieuRepository extends JpaRepository<ChiTieu, Integer> {
    List<ChiTieu> findByNgayChiBetween(LocalDateTime start, LocalDateTime end);

    List<ChiTieu> findByFlagDeleteOrderByNgayChiDesc(Integer flagDelete);

    // MỚI: Dành riêng cho phân trang danh sách khoản chi
    Page<ChiTieu> findByFlagDeleteOrderByNgayChiDesc(Integer flagDelete, Pageable pageable);

    Optional<ChiTieu> findByMaChiTieuAndFlagDelete(Integer id, Integer flagDelete);
}