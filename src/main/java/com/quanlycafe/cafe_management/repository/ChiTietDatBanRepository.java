package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.ChiTietDatBan;
import com.quanlycafe.cafe_management.entity.ChiTietDatBanId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChiTietDatBanRepository extends JpaRepository<ChiTietDatBan, ChiTietDatBanId> {
}