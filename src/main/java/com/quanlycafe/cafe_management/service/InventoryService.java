package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.entity.DonViTinh;
import com.quanlycafe.cafe_management.entity.HangHoa;
import com.quanlycafe.cafe_management.repository.DonViTinhRepository;
import com.quanlycafe.cafe_management.repository.HangHoaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final HangHoaRepository hangHoaRepository;
    private final DonViTinhRepository donViTinhRepository;

    public List<HangHoa> getAllItems() {
        return hangHoaRepository.findAll(Sort.by(Sort.Direction.ASC, "maHangHoa"));
    }

    public List<HangHoa> searchItems(String keyword) {
        return hangHoaRepository.findByTenHangHoaContainingIgnoreCaseOrderByMaHangHoaAsc(keyword);
    }

    public List<DonViTinh> getAllUnits() {
        return donViTinhRepository.findAll();
    }

    @Transactional
    public void createItem(String tenHangHoa, Double soLuong, Integer maDonViTinh, Double donGia) {
        DonViTinh unit = donViTinhRepository.findById(maDonViTinh).orElse(null);
        HangHoa item = new HangHoa();
        item.setTenHangHoa(tenHangHoa);
        item.setSoLuong(soLuong != null ? BigDecimal.valueOf(soLuong) : BigDecimal.ZERO);
        item.setDonViTinh(unit);
        item.setDonGia(BigDecimal.valueOf(donGia));
        hangHoaRepository.save(item);
    }

    @Transactional
    public void updateItem(Integer maHangHoa, String tenHangHoa, Integer maDonViTinh, Double donGia) {
        HangHoa item = hangHoaRepository.findById(maHangHoa).orElseThrow();
        DonViTinh unit = donViTinhRepository.findById(maDonViTinh).orElse(null);
        item.setTenHangHoa(tenHangHoa);
        item.setDonViTinh(unit);
        item.setDonGia(BigDecimal.valueOf(donGia));
        hangHoaRepository.save(item);
    }

    @Transactional
    public void deleteItem(Integer maHangHoa) {
        hangHoaRepository.deleteById(maHangHoa);
    }

    // Nghiệp vụ NHẬP KHO (Cộng thêm số lượng)
    @Transactional
    public void importStock(Integer maHangHoa, Double soLuongNhap) {
        HangHoa item = hangHoaRepository.findById(maHangHoa).orElseThrow();
        BigDecimal currentQty = item.getSoLuong() != null ? item.getSoLuong() : BigDecimal.ZERO;
        item.setSoLuong(currentQty.add(BigDecimal.valueOf(soLuongNhap)));
        hangHoaRepository.save(item);
    }

    // Nghiệp vụ XUẤT KHO (Trừ đi số lượng)
    @Transactional
    public void exportStock(Integer maHangHoa, Double soLuongXuat) {
        HangHoa item = hangHoaRepository.findById(maHangHoa).orElseThrow();
        BigDecimal currentQty = item.getSoLuong() != null ? item.getSoLuong() : BigDecimal.ZERO;
        BigDecimal newQty = currentQty.subtract(BigDecimal.valueOf(soLuongXuat));

        if (newQty.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Số lượng xuất vượt quá tồn kho!");
        }
        item.setSoLuong(newQty);
        hangHoaRepository.save(item);
    }
}