package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.dto.MenuFormDTO;
import com.quanlycafe.cafe_management.entity.ThucDon;
import com.quanlycafe.cafe_management.repository.ThucDonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * MenuService
 * * Version 1.2
 * * Date: 30-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 30-05-2026 Quản Lý      Apply MenuFormDTO & format convention
 * 30-05-2026 Quản Lý      Apply Pagination
 */
@Service
@RequiredArgsConstructor
public class MenuService {

    private final ThucDonRepository thucDonRepository;

    /**
     * Lấy tất cả các món (Có phân trang)
     *
     * @param pageable Pageable
     * @return Page<ThucDon>
     */
    public Page<ThucDon> getAllMenuItems(Pageable pageable) {
        return thucDonRepository.findAll(pageable);
    }

    /**
     * Lấy các món theo loại (Có phân trang)
     *
     * @param category String
     * @param pageable Pageable
     * @return Page<ThucDon>
     */
    public Page<ThucDon> getMenuItemsByCategory(String category, Pageable pageable) {
        if (category == null || category.trim().isEmpty() || category.equalsIgnoreCase("all")) {
            return getAllMenuItems(pageable);
        }
        return thucDonRepository.findByLoaiMon(category, pageable);
    }

    /**
     * Tìm kiếm món (Có phân trang)
     *
     * @param keyword  String
     * @param pageable Pageable
     * @return Page<ThucDon>
     */
    public Page<ThucDon> searchMenuItems(String keyword, Pageable pageable) {
        return thucDonRepository.findByTenMonContainingIgnoreCase(keyword, pageable);
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