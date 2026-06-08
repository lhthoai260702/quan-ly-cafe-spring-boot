package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.DonViTinh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DonViTinhRepository
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 */
@Repository
public interface DonViTinhRepository extends JpaRepository<DonViTinh, Integer> {
}