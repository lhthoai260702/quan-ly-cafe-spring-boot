package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer> {
    List<HoaDon> findByNgayGioTaoBetweenAndTrangThai(LocalDateTime start, LocalDateTime end, String trangThai);
}