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
 * Version 1.4
 * Date: 09-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 29-05-2026   lhthoai     Create
 * 30-05-2026   lhthoai     Apply PromotionFormDTO & format convention
 * 30-05-2026   lhthoai     Apply Pagination
 * 08-06-2026   lhthoai     Implement soft delete logic
 * 09-06-2026   lhthoai     Apply Java Coding Convention & Javadoc
 */
@Service
@RequiredArgsConstructor
public class MarketingService {

    private final KhuyenMaiRepository khuyenMaiRepository;

    /**
     * Lấy tất cả các khuyến mãi có phân trang.
     *
     * @param pageable Đối tượng phân trang
     * @return Trang chứa danh sách KhuyenMai
     */
    public Page<KhuyenMai> getAllPromotions(Pageable pageable) {
        return khuyenMaiRepository.findAll(pageable);
    }

    /**
     * Tìm kiếm khuyến mãi theo từ khóa có phân trang.
     *
     * @param keyword  Từ khóa tìm kiếm theo tên
     * @param pageable Đối tượng phân trang
     * @return Trang chứa danh sách KhuyenMai tìm được
     */
    public Page<KhuyenMai> searchPromotions(String keyword, Pageable pageable) {
        return khuyenMaiRepository.findByTenKhuyenMaiContainingIgnoreCaseOrderByMaKhuyenMaiDesc(keyword, pageable);
    }

    /**
     * Tự động xác định trạng thái của khuyến mãi dựa vào ngày hiện tại.
     *
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Chuỗi trạng thái (Sắp diễn ra, Đang diễn ra, Đã kết thúc)
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
     * Tạo mới khuyến mãi vào hệ thống.
     *
     * @param form Dữ liệu khuyến mãi từ Form
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
        km.setFlagDelete(0);

        khuyenMaiRepository.save(km);
    }

    /**
     * Cập nhật thông tin khuyến mãi.
     *
     * @param form Dữ liệu khuyến mãi từ Form
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
     * Xóa mềm khuyến mãi (chuyển trạng thái flag_delete = 1).
     *
     * @param maKhuyenMai Mã khuyến mãi cần xóa
     */
    @Transactional
    public void deletePromotion(Integer maKhuyenMai) {
        KhuyenMai km = khuyenMaiRepository.findById(maKhuyenMai)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Khuyến mãi"));

        km.setFlagDelete(1);
        khuyenMaiRepository.save(km);
    }

}