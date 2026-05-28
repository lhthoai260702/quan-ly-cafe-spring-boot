package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.KhuyenMai;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KhuyenMaiRepository extends JpaRepository<KhuyenMai, Integer> {
    List<KhuyenMai> findByTenKhuyenMaiContainingIgnoreCaseOrderByMaKhuyenMaiDesc(String keyword);
}