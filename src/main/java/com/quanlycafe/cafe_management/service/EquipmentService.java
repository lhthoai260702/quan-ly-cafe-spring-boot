package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.dto.EquipmentFormDTO;
import com.quanlycafe.cafe_management.entity.ThietBi;
import com.quanlycafe.cafe_management.repository.ThietBiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * EquipmentService
 * Version 1.4
 * Date: 09-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 29-05-2026   lhthoai     Create
 * 30-05-2026   lhthoai     Apply EquipmentFormDTO & format convention
 * 06-06-2026   lhthoai     Apply Pagination and Sorting
 * 07-06-2026   lhthoai     Update match new DB, add Status filter, apply soft delete
 * 09-06-2026   lhthoai     Apply Java Coding Convention & Add Javadoc
 */
@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final ThietBiRepository thietBiRepository;

    /**
     * Tìm kiếm thiết bị theo tên và trạng thái có phân trang.
     *
     * @param keyword  Từ khóa tìm kiếm theo tên
     * @param status   Trạng thái thiết bị cần lọc
     * @param pageable Đối tượng phân trang
     * @return Trang chứa danh sách thiết bị thỏa mãn điều kiện
     */
    public Page<ThietBi> searchEquipment(String keyword, String status, Pageable pageable) {
        if ("Tất cả".equalsIgnoreCase(status) || status.isEmpty()) {
            return thietBiRepository.findByTenThietBiContainingIgnoreCaseAndFlagDelete(keyword, 0, pageable);
        } else {
            return thietBiRepository.findByTenThietBiContainingIgnoreCaseAndTinhTrangAndFlagDelete(keyword, status, 0, pageable);
        }
    }

    /**
     * Tạo mới thiết bị trong hệ thống.
     *
     * @param form Form dữ liệu thiết bị từ View
     */
    @Transactional
    public void createEquipment(EquipmentFormDTO form) {
        ThietBi thietBi = new ThietBi();
        thietBi.setTenThietBi(form.getTenThietBi());
        thietBi.setTinhTrang(form.getTinhTrang());
        thietBi.setGhiChu(form.getGhiChu());
        thietBi.setNgayMua(form.getNgayMua());
        thietBi.setDonGiaMua(form.getDonGiaMua() != null ? BigDecimal.valueOf(form.getDonGiaMua()) : null);
        thietBi.setFlagDelete(0);

        thietBiRepository.save(thietBi);
    }

    /**
     * Cập nhật thông tin thiết bị.
     *
     * @param form Form dữ liệu thiết bị từ View
     */
    @Transactional
    public void updateEquipment(EquipmentFormDTO form) {
        ThietBi thietBi = thietBiRepository.findById(form.getMaThietBi())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));

        thietBi.setTenThietBi(form.getTenThietBi());
        thietBi.setTinhTrang(form.getTinhTrang());
        thietBi.setGhiChu(form.getGhiChu());
        thietBi.setNgayMua(form.getNgayMua());
        thietBi.setDonGiaMua(form.getDonGiaMua() != null ? BigDecimal.valueOf(form.getDonGiaMua()) : null);

        thietBiRepository.save(thietBi);
    }

    /**
     * Xóa mềm thiết bị (Đổi cờ flag_delete = 1).
     *
     * @param maThietBi Mã thiết bị cần xóa
     */
    @Transactional
    public void deleteEquipment(Integer maThietBi) {
        ThietBi thietBi = thietBiRepository.findById(maThietBi)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị để xóa"));

        thietBi.setFlagDelete(1);
        thietBiRepository.save(thietBi);
    }
}