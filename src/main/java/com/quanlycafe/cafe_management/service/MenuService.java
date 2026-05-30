package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.dto.MenuFormDTO;
import com.quanlycafe.cafe_management.entity.ThucDon;
import com.quanlycafe.cafe_management.repository.ThucDonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * MenuService
 * * Version 1.1
 * * Date: 29-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 30-05-2026 Quản Lý      Apply MenuFormDTO & format convention
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
     * @param form MenuFormDTO
     */
    @Transactional
    public void createMenuItem(MenuFormDTO form) {
        ThucDon mon = new ThucDon();
        mon.setTenMon(form.getTenMon());
        mon.setGiaTienHienTai(BigDecimal.valueOf(form.getGiaTienHienTai()));
        mon.setLoaiMon(form.getLoaiMon());

        thucDonRepository.save(mon);
    }

    /**
     * Sửa món
     *
     * @param form MenuFormDTO
     */
    @Transactional
    public void updateMenuItem(MenuFormDTO form) {
        ThucDon mon = thucDonRepository.findById(form.getMaThucDon())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món trong thực đơn"));

        mon.setTenMon(form.getTenMon());
        mon.setGiaTienHienTai(BigDecimal.valueOf(form.getGiaTienHienTai()));
        mon.setLoaiMon(form.getLoaiMon());

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