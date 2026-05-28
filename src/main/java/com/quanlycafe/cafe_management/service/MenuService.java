package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.entity.ThucDon;
import com.quanlycafe.cafe_management.repository.ThucDonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final ThucDonRepository thucDonRepository;

    public List<ThucDon> getAllMenuItems() {
        return thucDonRepository.findAll();
    }

    public List<ThucDon> getMenuItemsByCategory(String category) {
        if (category == null || category.trim().isEmpty() || category.equalsIgnoreCase("all")) {
            return getAllMenuItems();
        }
        return thucDonRepository.findByLoaiMon(category);
    }

    public List<ThucDon> searchMenuItems(String keyword) {
        return thucDonRepository.findByTenMonContainingIgnoreCase(keyword);
    }

    public List<String> getAllCategories() {
        return thucDonRepository.findDistinctLoaiMon();
    }

    @Transactional
    public void createMenuItem(String tenMon, Double giaTienHienTai, String loaiMon) {
        ThucDon mon = new ThucDon();
        mon.setTenMon(tenMon);
        mon.setGiaTienHienTai(BigDecimal.valueOf(giaTienHienTai));
        mon.setLoaiMon(loaiMon);
        thucDonRepository.save(mon);
    }

    @Transactional
    public void updateMenuItem(Integer maThucDon, String tenMon, Double giaTienHienTai, String loaiMon) {
        ThucDon mon = thucDonRepository.findById(maThucDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món trong thực đơn"));

        mon.setTenMon(tenMon);
        mon.setGiaTienHienTai(BigDecimal.valueOf(giaTienHienTai));
        mon.setLoaiMon(loaiMon);
        thucDonRepository.save(mon);
    }

    @Transactional
    public void deleteMenuItem(Integer maThucDon) {
        if (thucDonRepository.existsById(maThucDon)) {
            thucDonRepository.deleteById(maThucDon);
        } else {
            throw new RuntimeException("Không tìm thấy món để xóa");
        }
    }
}