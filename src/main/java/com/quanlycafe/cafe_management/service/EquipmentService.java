package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.dto.EquipmentFormDTO;
import com.quanlycafe.cafe_management.entity.ThietBi;
import com.quanlycafe.cafe_management.repository.ThietBiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * EquipmentService
 * * Version 1.1
 * * Date: 29-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 30-05-2026 Quản Lý      Apply EquipmentFormDTO & format convention
 */
@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final ThietBiRepository thietBiRepository;

    /**
     * Lấy tất cả thiết bị
     *
     * @return List<ThietBi>
     */
    public List<ThietBi> getAllEquipments() {
        return thietBiRepository.findAll();
    }

    /**
     * Tìm kiếm thiết bị theo tên
     *
     * @param keyword String
     * @return List<ThietBi>
     */
    public List<ThietBi> searchEquipment(String keyword) {
        return thietBiRepository.findByTenThietBiContainingIgnoreCase(keyword);
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