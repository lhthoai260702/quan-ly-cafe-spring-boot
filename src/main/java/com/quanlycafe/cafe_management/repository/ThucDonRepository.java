package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.ThucDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThucDonRepository extends JpaRepository<ThucDon, Integer> {

    List<ThucDon> findByLoaiMon(String loaiMon);
    List<ThucDon> findByTenMonContainingIgnoreCase(String keyword);
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT t.loaiMon FROM ThucDon t WHERE t.loaiMon IS NOT NULL")
    List<String> findDistinctLoaiMon();
}