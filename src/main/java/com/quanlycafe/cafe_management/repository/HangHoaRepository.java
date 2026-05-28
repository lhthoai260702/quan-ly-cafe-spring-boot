package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.HangHoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HangHoaRepository extends JpaRepository<HangHoa, Integer> {
    List<HangHoa> findByTenHangHoaContainingIgnoreCaseOrderByMaHangHoaAsc(String keyword);
}