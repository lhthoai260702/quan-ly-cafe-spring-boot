package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.Ban;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BanRepository extends JpaRepository<Ban, Integer> {
}