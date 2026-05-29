package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.entity.ThietBi;
import com.quanlycafe.cafe_management.repository.ThietBiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * EquipmentService
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
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
     * @param tenThietBi String
     * @param soLuong    Integer
     * @param ghiChu     String
     * @param ngayMua    LocalDate
     * @param donGiaMua  Double
     */
    @Transactional
    public void createEquipment(String tenThietBi, Integer soLuong, String ghiChu, LocalDate ngayMua, Double donGiaMua) {
        ThietBi thietBi = new ThietBi();
        thietBi.setTenThietBi(tenThietBi);
        thietBi.setSoLuong(soLuong != null ? soLuong : 0);
        thietBi.setGhiChu(ghiChu);
        thietBi.setNgayMua(ngayMua);
        thietBi.setDonGiaMua(donGiaMua != null ? BigDecimal.valueOf(donGiaMua) : null);

        thietBiRepository.save(thietBi);
    }

    /**
     * Cập nhật thiết bị
     *
     * @param maThietBi  Integer
     * @param tenThietBi String
     * @param soLuong    Integer
     * @param ghiChu     String
     * @param ngayMua    LocalDate
     * @param donGiaMua  Double
     */
    @Transactional
    public void updateEquipment(Integer maThietBi, String tenThietBi, Integer soLuong, String ghiChu, LocalDate ngayMua, Double donGiaMua) {
        ThietBi thietBi = thietBiRepository.findById(maThietBi)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thiết bị"));

        thietBi.setTenThietBi(tenThietBi);
        thietBi.setSoLuong(soLuong != null ? soLuong : 0);
        thietBi.setGhiChu(ghiChu);
        thietBi.setNgayMua(ngayMua);
        thietBi.setDonGiaMua(donGiaMua != null ? BigDecimal.valueOf(donGiaMua) : null);

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