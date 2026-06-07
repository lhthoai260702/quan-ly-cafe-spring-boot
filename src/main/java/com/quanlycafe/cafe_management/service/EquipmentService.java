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
 * Version 1.3
 * Date: 07-06-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 30-05-2026 Quản Lý      Apply EquipmentFormDTO & format convention
 * 06-06-2026 Quản Lý      Apply Pagination and Sorting
 * 07-06-2026 Quản Lý      Update match new DB, add Status filter, apply soft delete
 */
@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final ThietBiRepository thietBiRepository;

    /**
     * Tìm kiếm thiết bị theo tên và trạng thái có phân trang
     *
     * @param keyword  String
     * @param status   String
     * @param pageable Pageable
     * @return Page<ThietBi>
     */
    public Page<ThietBi> searchEquipment(String keyword, String status, Pageable pageable) {
        if ("Tất cả".equalsIgnoreCase(status) || status.isEmpty()) {
            return thietBiRepository.findByTenThietBiContainingIgnoreCaseAndFlagDelete(keyword, 0, pageable);
        } else {
            return thietBiRepository.findByTenThietBiContainingIgnoreCaseAndTinhTrangAndFlagDelete(keyword, status, 0, pageable);
        }
    }

    /**
     * Tạo mới thiết bị
     *
     * @param form EquipmentFormDTO
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
     * Cập nhật thiết bị
     *
     * @param form EquipmentFormDTO
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
     * Xóa mềm thiết bị (Đổi cờ flag_delete = 1)
     *
     * @param maThietBi Integer
     */
    @Transactional
    public void deleteEquipment(Integer maThietBi) {
        ThietBi thietBi = thietBiRepository.findById(maThietBi)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị để xóa"));

        thietBi.setFlagDelete(1);
        thietBiRepository.save(thietBi);
    }
}