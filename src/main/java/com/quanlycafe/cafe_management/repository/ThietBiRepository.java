package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.ThietBi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThietBiRepository extends JpaRepository<ThietBi, Integer> {
    List<ThietBi> findByTenThietBiContainingIgnoreCase(String keyword);
}