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

/**
 * MarketingService
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 */
@Service
@RequiredArgsConstructor
public class MarketingService {

    private final KhuyenMaiRepository khuyenMaiRepository;

    /**
     * Lấy tất cả các khuyến mãi
     *
     * @return List<KhuyenMai>
     */
    public List<KhuyenMai> getAllPromotions() {
        return khuyenMaiRepository.findAll(Sort.by(Sort.Direction.DESC, "maKhuyenMai"));
    }

    /**
     * Tìm kiếm khuyến mãi
     *
     * @param keyword String
     * @return List<KhuyenMai>
     */
    public List<KhuyenMai> searchPromotions(String keyword) {
        return khuyenMaiRepository.findByTenKhuyenMaiContainingIgnoreCaseOrderByMaKhuyenMaiDesc(keyword);
    }

    /**
     * Tự động tính trạng thái dựa vào ngày hiện tại
     *
     * @param startDate LocalDate
     * @param endDate   LocalDate
     * @return String
     */
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

    /**
     * Tạo khuyến mãi
     *
     * @param tenKhuyenMai  String
     * @param ngayBatDau    LocalDate
     * @param ngayKetThuc   LocalDate
     * @param loaiKhuyenMai String
     * @param giaTriGiam    Double
     * @param moTa          String
     */
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

    /**
     * Sửa khuyến mãi
     *
     * @param maKhuyenMai   Integer
     * @param tenKhuyenMai  String
     * @param ngayBatDau    LocalDate
     * @param ngayKetThuc   LocalDate
     * @param loaiKhuyenMai String
     * @param giaTriGiam    Double
     * @param moTa          String
     */
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

    /**
     * Xóa khuyến mãi
     *
     * @param maKhuyenMai Integer
     */
    @Transactional
    public void deletePromotion(Integer maKhuyenMai) {
        khuyenMaiRepository.deleteById(maKhuyenMai);
    }
}