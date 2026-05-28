package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.DonViTinh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DonViTinhRepository extends JpaRepository<DonViTinh, Integer> {
}