package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.ChiTietDatBan;
import com.quanlycafe.cafe_management.entity.ChiTietDatBanId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ChiTietDatBanRepository
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 */
@Repository
public interface ChiTietDatBanRepository extends JpaRepository<ChiTietDatBan, ChiTietDatBanId> {
}