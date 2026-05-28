package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.entity.KhuyenMai;
import com.quanlycafe.cafe_management.repository.KhuyenMaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MarketingService {

    private final KhuyenMaiRepository khuyenMaiRepository;

    public List<KhuyenMai> getAllPromotions() {
        return khuyenMaiRepository.findAll(Sort.by(Sort.Direction.DESC, "maKhuyenMai"));
    }

    public List<KhuyenMai> searchPromotions(String keyword) {
        return khuyenMaiRepository.findByTenKhuyenMaiContainingIgnoreCaseOrderByMaKhuyenMaiDesc(keyword);
    }

    // Hàm tiện ích: Tự động tính trạng thái dựa vào ngày hiện tại
    private String determineStatus(LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        if (today.isBefore(startDate)) {
            return "Sắp diễn ra";
        } else if (today.isAfter(endDate)) {
            return "Đã kết thúc";
        } else {
            return "Đang diễn ra";
        }
    }

    @Transactional
    public void createPromotion(String tenKhuyenMai, LocalDate ngayBatDau, LocalDate ngayKetThuc,
                                String loaiKhuyenMai, Double giaTriGiam, String moTa) {
        KhuyenMai km = new KhuyenMai();
        km.setTenKhuyenMai(tenKhuyenMai);
        km.setNgayBatDau(ngayBatDau);
        km.setNgayKetThuc(ngayKetThuc);
        km.setLoaiKhuyenMai(loaiKhuyenMai);
        km.setGiaTriGiam(BigDecimal.valueOf(giaTriGiam));
        km.setMoTa(moTa);
        km.setTrangThai(determineStatus(ngayBatDau, ngayKetThuc));

        khuyenMaiRepository.save(km);
    }

    @Transactional
    public void updatePromotion(Integer maKhuyenMai, String tenKhuyenMai, LocalDate ngayBatDau,
                                LocalDate ngayKetThuc, String loaiKhuyenMai, Double giaTriGiam, String moTa) {
        KhuyenMai km = khuyenMaiRepository.findById(maKhuyenMai)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Khuyến mãi"));

        km.setTenKhuyenMai(tenKhuyenMai);
        km.setNgayBatDau(ngayBatDau);
        km.setNgayKetThuc(ngayKetThuc);
        km.setLoaiKhuyenMai(loaiKhuyenMai);
        km.setGiaTriGiam(BigDecimal.valueOf(giaTriGiam));
        km.setMoTa(moTa);
        km.setTrangThai(determineStatus(ngayBatDau, ngayKetThuc));

        khuyenMaiRepository.save(km);
    }

    @Transactional
    public void deletePromotion(Integer maKhuyenMai) {
        khuyenMaiRepository.deleteById(maKhuyenMai);
    }
}