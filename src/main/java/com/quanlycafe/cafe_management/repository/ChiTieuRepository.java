package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.ChiTieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ChiTieuRepository extends JpaRepository<ChiTieu, Integer> {
    List<ChiTieu> findByNgayChiBetween(LocalDateTime start, LocalDateTime end);
}