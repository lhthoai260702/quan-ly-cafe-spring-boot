package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.dto.PromotionFormDTO;
import com.quanlycafe.cafe_management.entity.KhuyenMai;
import com.quanlycafe.cafe_management.repository.KhuyenMaiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * MarketingService
 * <p>
 * Version 1.3
 * <p>
 * Date: 08-06-2026
 * <p>
 * Copyright
 * <p>
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 * 30-05-2026 lhthoai       Apply PromotionFormDTO & format convention
 * 30-05-2026 lhthoai       Apply Pagination
 * 08-06-2026 lhthoai      Implement soft delete logic
 */
@Service
@RequiredArgsConstructor
public class MarketingService {

    private final KhuyenMaiRepository khuyenMaiRepository;

    /**
     * Lấy tất cả các khuyến mãi (Có phân trang)
     *
     * @param pageable Pageable
     * @return Page<KhuyenMai>
     */
    public Page<KhuyenMai> getAllPromotions(Pageable pageable) {
        return khuyenMaiRepository.findAll(pageable);
    }

    /**
     * Tìm kiếm khuyến mãi (Có phân trang)
     *
     * @param keyword  String
     * @param pageable Pageable
     * @return Page<KhuyenMai>
     */
    public Page<KhuyenMai> searchPromotions(String keyword, Pageable pageable) {
        return khuyenMaiRepository.findByTenKhuyenMaiContainingIgnoreCaseOrderByMaKhuyenMaiDesc(keyword, pageable);
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
     * @param form PromotionFormDTO
     */
    @Transactional
    public void createPromotion(PromotionFormDTO form) {
        KhuyenMai km = new KhuyenMai();
        km.setTenKhuyenMai(form.getTenKhuyenMai());
        km.setNgayBatDau(form.getNgayBatDau());
        km.setNgayKetThuc(form.getNgayKetThuc());
        km.setLoaiKhuyenMai(form.getLoaiKhuyenMai());
        km.setGiaTriGiam(BigDecimal.valueOf(form.getGiaTriGiam()));
        km.setMoTa(form.getMoTa());
        km.setTrangThai(determineStatus(form.getNgayBatDau(), form.getNgayKetThuc()));

        // Mặc định gán cờ 0 (hiển thị) khi tạo mới
        km.setFlagDelete(0);

        khuyenMaiRepository.save(km);
    }

    /**
     * Sửa khuyến mãi
     *
     * @param form PromotionFormDTO
     */
    @Transactional
    public void updatePromotion(PromotionFormDTO form) {
        KhuyenMai km = khuyenMaiRepository.findById(form.getMaKhuyenMai())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Khuyến mãi"));

        km.setTenKhuyenMai(form.getTenKhuyenMai());
        km.setNgayBatDau(form.getNgayBatDau());
        km.setNgayKetThuc(form.getNgayKetThuc());
        km.setLoaiKhuyenMai(form.getLoaiKhuyenMai());
        km.setGiaTriGiam(BigDecimal.valueOf(form.getGiaTriGiam()));
        km.setMoTa(form.getMoTa());
        km.setTrangThai(determineStatus(form.getNgayBatDau(), form.getNgayKetThuc()));

        khuyenMaiRepository.save(km);
    }

    /**
     * Xóa khuyến mãi (Xóa mềm - Chuyển cờ)
     *
     * @param maKhuyenMai Integer
     */
    @Transactional
    public void deletePromotion(Integer maKhuyenMai) {
        KhuyenMai km = khuyenMaiRepository.findById(maKhuyenMai)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Khuyến mãi"));

        km.setFlagDelete(1);

        khuyenMaiRepository.save(km);
    }
}