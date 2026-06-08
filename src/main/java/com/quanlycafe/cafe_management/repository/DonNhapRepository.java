package com.quanlycafe.cafe_management.repository;

import com.quanlycafe.cafe_management.entity.DonNhap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DonNhapRepository extends JpaRepository<DonNhap, Integer> {
    List<DonNhap> findByHangHoa_MaHangHoaAndFlagDeleteOrderByNgayNhapDesc(Integer maHangHoa, Integer flagDelete);

    List<DonNhap> findByNgayNhapBetween(LocalDateTime start, LocalDateTime end);
}