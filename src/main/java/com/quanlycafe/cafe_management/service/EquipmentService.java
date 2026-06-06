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
 * Version 1.2
 * Date: 29-05-2026
 * Copyright
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 30-05-2026 Quản Lý      Apply EquipmentFormDTO & format convention
 * 06-06-2026 Quản Lý      Apply Pagination and Sorting
 */
@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final ThietBiRepository thietBiRepository;

    /**
     * Lấy tất cả thiết bị có phân trang
     *
     * @param pageable Pageable
     * @return Page<ThietBi>
     */
    public Page<ThietBi> getAllEquipments(Pageable pageable) {
        return thietBiRepository.findAll(pageable);
    }

    /**
     * Tìm kiếm thiết bị theo tên có phân trang
     *
     * @param keyword  String
     * @param pageable Pageable
     * @return Page<ThietBi>
     */
    public Page<ThietBi> searchEquipment(String keyword, Pageable pageable) {
        return thietBiRepository.findByTenThietBiContainingIgnoreCase(keyword, pageable);
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
        thietBi.setSoLuong(form.getSoLuong() != null ? form.getSoLuong() : 0);
        thietBi.setGhiChu(form.getGhiChu());
        thietBi.setNgayMua(form.getNgayMua());
        thietBi.setDonGiaMua(form.getDonGiaMua() != null ? BigDecimal.valueOf(form.getDonGiaMua()) : null);

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
        thietBi.setSoLuong(form.getSoLuong() != null ? form.getSoLuong() : 0);
        thietBi.setGhiChu(form.getGhiChu());
        thietBi.setNgayMua(form.getNgayMua());
        thietBi.setDonGiaMua(form.getDonGiaMua() != null ? BigDecimal.valueOf(form.getDonGiaMua()) : null);

        thietBiRepository.save(thietBi);
    }

    /**
     * Xóa thiết bị
     *
     * @param maThietBi Integer
     */
    @Transactional
    public void deleteEquipment(Integer maThietBi) {
        if (thietBiRepository.existsById(maThietBi)) {
            thietBiRepository.deleteById(maThietBi);
        } else {
            throw new RuntimeException("Không tìm thấy thiết bị để xóa");
        }
    }
}