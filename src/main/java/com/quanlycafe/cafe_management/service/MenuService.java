package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.entity.ThucDon;
import com.quanlycafe.cafe_management.repository.ThucDonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * MenuService
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 */
@Service
@RequiredArgsConstructor
public class MenuService {

    private final ThucDonRepository thucDonRepository;

    /**
     * Lấy tất cả các món
     *
     * @return List<ThucDon>
     */
    public List<ThucDon> getAllMenuItems() {
        return thucDonRepository.findAll();
    }

    /**
     * Lấy các món theo loại
     *
     * @param category String
     * @return List<ThucDon>
     */
    public List<ThucDon> getMenuItemsByCategory(String category) {
        if (category == null || category.trim().isEmpty() || category.equalsIgnoreCase("all")) {
            return getAllMenuItems();
        }
        return thucDonRepository.findByLoaiMon(category);
    }

    /**
     * Tìm kiếm món
     *
     * @param keyword String
     * @return List<ThucDon>
     */
    public List<ThucDon> searchMenuItems(String keyword) {
        return thucDonRepository.findByTenMonContainingIgnoreCase(keyword);
    }

    /**
     * Lấy danh sách loại món
     *
     * @return List<String>
     */
    public List<String> getAllCategories() {
        return thucDonRepository.findDistinctLoaiMon();
    }

    /**
     * Tạo món mới
     *
     * @param tenMon         String
     * @param giaTienHienTai Double
     * @param loaiMon        String
     */
    @Transactional
    public void createMenuItem(String tenMon, Double giaTienHienTai, String loaiMon) {
        ThucDon mon = new ThucDon();
        mon.setTenMon(tenMon);
        mon.setGiaTienHienTai(BigDecimal.valueOf(giaTienHienTai));
        mon.setLoaiMon(loaiMon);

        thucDonRepository.save(mon);
    }

    /**
     * Sửa món
     *
     * @param maThucDon      Integer
     * @param tenMon         String
     * @param giaTienHienTai Double
     * @param loaiMon        String
     */
    @Transactional
    public void updateMenuItem(Integer maThucDon, String tenMon, Double giaTienHienTai, String loaiMon) {
        ThucDon mon = thucDonRepository.findById(maThucDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món trong thực đơn"));

        mon.setTenMon(tenMon);
        mon.setGiaTienHienTai(BigDecimal.valueOf(giaTienHienTai));
        mon.setLoaiMon(loaiMon);

        thucDonRepository.save(mon);
    }

    /**
     * Xóa món
     *
     * @param maThucDon Integer
     */
    @Transactional
    public void deleteMenuItem(Integer maThucDon) {
        if (thucDonRepository.existsById(maThucDon)) {
            thucDonRepository.deleteById(maThucDon);
        } else {
            throw new RuntimeException("Không tìm thấy món để xóa");
        }
    }
}