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
 * Version 1.5
 * Date: 09-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 29-05-2026   lhthoai     Create
 * 30-05-2026   lhthoai     Apply MenuFormDTO & format convention
 * 07-06-2026   lhthoai     Add logic to save Ingredients & Auto-fetch DonViTinh
 * 09-06-2026   lhthoai     Apply Java Coding Convention & Javadoc
 */
@Service
@RequiredArgsConstructor
public class MenuService {

    private final ThucDonRepository thucDonRepository;
    private final ChiTietThucDonRepository chiTietThucDonRepository;
    private final HangHoaRepository hangHoaRepository;

    /**
     * Lấy danh sách tất cả các món trong thực đơn (có phân trang).
     *
     * @param pageable Đối tượng phân trang
     * @return Trang chứa danh sách ThucDon
     */
    public Page<ThucDon> getAllMenuItems(Pageable pageable) {
        return thucDonRepository.findAll(pageable);
    }

    /**
     * Lấy các món theo loại (có phân trang).
     *
     * @param category Loại món cần lọc
     * @param pageable Đối tượng phân trang
     * @return Trang chứa danh sách ThucDon theo loại
     */
    public Page<ThucDon> getMenuItemsByCategory(String category, Pageable pageable) {
        if (category == null || category.trim().isEmpty() || category.equalsIgnoreCase("all")) {
            return getAllMenuItems(pageable);
        }
        return thucDonRepository.findByLoaiMon(category, pageable);
    }

    /**
     * Tìm kiếm các món theo từ khóa (có phân trang).
     *
     * @param keyword  Từ khóa tìm kiếm
     * @param pageable Đối tượng phân trang
     * @return Trang chứa danh sách ThucDon tìm được
     */
    public Page<ThucDon> searchMenuItems(String keyword, Pageable pageable) {
        return thucDonRepository.findByTenMonContainingIgnoreCase(keyword, pageable);
    }

    /**
     * Lấy danh sách các loại món hiện có.
     *
     * @return Danh sách tên các loại món
     */
    public List<String> getAllCategories() {
        return thucDonRepository.findDistinctLoaiMon();
    }

    /**
     * Tạo món mới (kèm theo lưu công thức nguyên liệu và tự động lấy Đơn vị tính).
     *
     * @param form Form chứa dữ liệu thực đơn
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

                    hangHoaRepository.findById(ing.getMaHangHoa()).ifPresent(hh -> {
                        // Ưu tiên lấy Đơn vị sử dụng. Nếu không có mới lấy Đơn vị tính
                        if (hh.getDonViSuDung() != null) {
                            detail.setDonViTinh(hh.getDonViSuDung().getTenDonVi());
                        } else if (hh.getDonViTinh() != null) {
                            detail.setDonViTinh(hh.getDonViTinh().getTenDonVi());
                        }
                    });

                    chiTietThucDonRepository.save(detail);
                }
            }
        }
    }

    /**
     * Cập nhật món (kèm theo cập nhật công thức nguyên liệu và tự động lấy Đơn vị tính).
     *
     * @param form Form chứa dữ liệu thực đơn
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

                    hangHoaRepository.findById(ing.getMaHangHoa()).ifPresent(hh -> {
                        // Ưu tiên lấy Đơn vị sử dụng. Nếu không có mới lấy Đơn vị tính
                        if (hh.getDonViSuDung() != null) {
                            detail.setDonViTinh(hh.getDonViSuDung().getTenDonVi());
                        } else if (hh.getDonViTinh() != null) {
                            detail.setDonViTinh(hh.getDonViTinh().getTenDonVi());
                        }
                    });

                    chiTietThucDonRepository.save(detail);
                }
            }
        }
    }

    /**
     * Xóa món khỏi thực đơn (xóa mềm).
     *
     * @param maThucDon Mã món cần xóa
     */
    @Transactional
    public void deleteMenuItem(Integer maThucDon) {
        ThucDon mon = thucDonRepository.findById(maThucDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món để xóa"));
        mon.setFlagDelete(1);
        thucDonRepository.save(mon);
    }
}