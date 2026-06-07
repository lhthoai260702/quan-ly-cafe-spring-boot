package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.dto.MenuFormDTO;
import com.quanlycafe.cafe_management.entity.ChiTietThucDon;
import com.quanlycafe.cafe_management.entity.ThucDon;
import com.quanlycafe.cafe_management.repository.ChiTietThucDonRepository;
import com.quanlycafe.cafe_management.repository.HangHoaRepository;
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
 * <p>
 * Version 1.4
 * <p>
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai      Create
 * 30-05-2026 Quản Lý      Apply MenuFormDTO & format convention
 * 07-06-2026 Quản Lý      Add logic to save Ingredients & Auto-fetch DonViTinh
 */
@Service
@RequiredArgsConstructor
public class MenuService {

    private final ThucDonRepository thucDonRepository;
    private final ChiTietThucDonRepository chiTietThucDonRepository;
    private final HangHoaRepository hangHoaRepository; // Bổ sung kho Hàng Hóa

    /**
     * Lấy tất cả các món (Có phân trang)
     *
     * @param pageable
     * @return
     */
    public Page<ThucDon> getAllMenuItems(Pageable pageable) {
        return thucDonRepository.findAll(pageable);
    }

    /**
     * Lấy các món theo loại (Có phân trang)
     *
     * @param category
     * @param pageable
     * @return
     */
    public Page<ThucDon> getMenuItemsByCategory(String category, Pageable pageable) {
        if (category == null || category.trim().isEmpty() || category.equalsIgnoreCase("all")) {
            return getAllMenuItems(pageable);
        }
        return thucDonRepository.findByLoaiMon(category, pageable);
    }

    /**
     * Tìm kiếm các món theo từ khóa (Có phân trang)
     *
     * @param keyword
     * @param pageable
     * @return
     */
    public Page<ThucDon> searchMenuItems(String keyword, Pageable pageable) {
        return thucDonRepository.findByTenMonContainingIgnoreCase(keyword, pageable);
    }

    /**
     * Lấy danh sách các loại món
     *
     * @return
     */
    public List<String> getAllCategories() {
        return thucDonRepository.findDistinctLoaiMon();
    }

    /**
     * Tạo món mới (Kèm theo lưu công thức nguyên liệu và tự động lấy Đơn vị tính)
     *
     * @param form
     */
    @Transactional
    public void createMenuItem(MenuFormDTO form) {
        ThucDon mon = new ThucDon();
        mon.setTenMon(form.getTenMon());
        mon.setGiaTienHienTai(BigDecimal.valueOf(form.getGiaTienHienTai()));
        mon.setLoaiMon(form.getLoaiMon());
        mon.setFlagDelete(0);

        mon = thucDonRepository.save(mon);

        if (form.getIngredients() != null && !form.getIngredients().isEmpty()) {
            for (MenuFormDTO.IngredientDTO ing : form.getIngredients()) {
                if (ing.getMaHangHoa() != null && ing.getKhoiLuong() != null) {
                    ChiTietThucDon detail = new ChiTietThucDon();
                    detail.setMaThucDon(mon.getMaThucDon());
                    detail.setMaHangHoa(ing.getMaHangHoa());
                    detail.setKhoiLuong(BigDecimal.valueOf(ing.getKhoiLuong()));

                    // Logic thông minh: Tự động lấy tên Đơn vị tính từ Hàng Hóa
                    hangHoaRepository.findById(ing.getMaHangHoa()).ifPresent(hh -> {
                        if (hh.getDonViTinh() != null) {
                            detail.setDonViTinh(hh.getDonViTinh().getTenDonVi());
                        }
                    });

                    chiTietThucDonRepository.save(detail);
                }
            }
        }
    }

    /**
     *
     * @param form
     */
    @Transactional
    public void updateMenuItem(MenuFormDTO form) {
        ThucDon mon = thucDonRepository.findById(form.getMaThucDon())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món trong thực đơn"));

        mon.setTenMon(form.getTenMon());
        mon.setGiaTienHienTai(BigDecimal.valueOf(form.getGiaTienHienTai()));
        mon.setLoaiMon(form.getLoaiMon());

        thucDonRepository.save(mon);

        chiTietThucDonRepository.deleteByMaThucDon(mon.getMaThucDon());

        if (form.getIngredients() != null && !form.getIngredients().isEmpty()) {
            for (MenuFormDTO.IngredientDTO ing : form.getIngredients()) {
                if (ing.getMaHangHoa() != null && ing.getKhoiLuong() != null) {
                    ChiTietThucDon detail = new ChiTietThucDon();
                    detail.setMaThucDon(mon.getMaThucDon());
                    detail.setMaHangHoa(ing.getMaHangHoa());
                    detail.setKhoiLuong(BigDecimal.valueOf(ing.getKhoiLuong()));

                    // Logic thông minh: Tự động lấy tên Đơn vị tính từ Hàng Hóa
                    hangHoaRepository.findById(ing.getMaHangHoa()).ifPresent(hh -> {
                        if (hh.getDonViTinh() != null) {
                            detail.setDonViTinh(hh.getDonViTinh().getTenDonVi());
                        }
                    });

                    chiTietThucDonRepository.save(detail);
                }
            }
        }
    }

    /**
     * Xoá
     *
     * @param maThucDon
     */
    @Transactional
    public void deleteMenuItem(Integer maThucDon) {
        ThucDon mon = thucDonRepository.findById(maThucDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món để xóa"));
        mon.setFlagDelete(1);
        thucDonRepository.save(mon);
    }
}