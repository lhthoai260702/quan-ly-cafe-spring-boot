package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.dto.InventoryFormDTO;
import com.quanlycafe.cafe_management.dto.StockActionDTO;
import com.quanlycafe.cafe_management.entity.DonViTinh;
import com.quanlycafe.cafe_management.entity.HangHoa;
import com.quanlycafe.cafe_management.repository.DonViTinhRepository;
import com.quanlycafe.cafe_management.repository.HangHoaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * InventoryService
 * * Version 1.2
 * * Date: 30-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 30-05-2026 Quản Lý      Apply DTOs & format convention
 * 30-05-2026 Quản Lý      Apply Pagination
 */
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final HangHoaRepository hangHoaRepository;
    private final DonViTinhRepository donViTinhRepository;

    /**
     * Lấy tất cả các mặt hàng (Có phân trang)
     *
     * @param pageable Pageable
     * @return Page<HangHoa>
     */
    public Page<HangHoa> getAllItems(Pageable pageable) {
        return hangHoaRepository.findAll(pageable);
    }

    /**
     * Tìm kiếm mặt hàng theo tên (Có phân trang)
     *
     * @param keyword  String
     * @param pageable Pageable
     * @return Page<HangHoa>
     */
    public Page<HangHoa> searchItems(String keyword, Pageable pageable) {
        return hangHoaRepository.findByTenHangHoaContainingIgnoreCaseOrderByMaHangHoaAsc(keyword, pageable);
    }

    /**
     * Lấy tất cả các đơn vị tính
     *
     * @return List<DonViTinh>
     */
    public List<DonViTinh> getAllUnits() {
        return donViTinhRepository.findAll();
    }

    /**
     * Tạo mặt hàng mới
     *
     * @param form InventoryFormDTO
     */
    @Transactional
    public void createItem(InventoryFormDTO form) {
        DonViTinh unit = donViTinhRepository.findById(form.getMaDonViTinh()).orElse(null);
        HangHoa item = new HangHoa();
        item.setTenHangHoa(form.getTenHangHoa());
        item.setSoLuong(form.getSoLuong() != null ? BigDecimal.valueOf(form.getSoLuong()) : BigDecimal.ZERO);
        item.setDonViTinh(unit);
        item.setDonGia(BigDecimal.valueOf(form.getDonGia()));

        hangHoaRepository.save(item);
    }

    /**
     * Sửa thông tin mặt hàng
     *
     * @param form InventoryFormDTO
     */
    @Transactional
    public void updateItem(InventoryFormDTO form) {
        HangHoa item = hangHoaRepository.findById(form.getMaHangHoa()).orElseThrow();
        DonViTinh unit = donViTinhRepository.findById(form.getMaDonViTinh()).orElse(null);

        item.setTenHangHoa(form.getTenHangHoa());
        item.setDonViTinh(unit);
        item.setDonGia(BigDecimal.valueOf(form.getDonGia()));

        hangHoaRepository.save(item);
    }

    /**
     * Xóa mặt hàng
     *
     * @param maHangHoa Integer
     */
    @Transactional
    public void deleteItem(Integer maHangHoa) {
        hangHoaRepository.deleteById(maHangHoa);
    }

    /**
     * Nghiệp vụ NHẬP KHO (Cộng thêm số lượng)
     *
     * @param form StockActionDTO
     */
    @Transactional
    public void importStock(StockActionDTO form) {
        HangHoa item = hangHoaRepository.findById(form.getMaHangHoa()).orElseThrow();
        BigDecimal currentQty = item.getSoLuong() != null ? item.getSoLuong() : BigDecimal.ZERO;
        item.setSoLuong(currentQty.add(BigDecimal.valueOf(form.getSoLuongThaoTac())));

        hangHoaRepository.save(item);
    }

    /**
     * Nghiệp vụ XUẤT KHO (Trừ đi số lượng)
     *
     * @param form StockActionDTO
     * @throws RuntimeException Ném lỗi nếu xuất quá tồn kho
     */
    @Transactional
    public void exportStock(StockActionDTO form) {
        HangHoa item = hangHoaRepository.findById(form.getMaHangHoa()).orElseThrow();
        BigDecimal currentQty = item.getSoLuong() != null ? item.getSoLuong() : BigDecimal.ZERO;
        BigDecimal newQty = currentQty.subtract(BigDecimal.valueOf(form.getSoLuongThaoTac()));

        if (newQty.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Số lượng xuất vượt quá tồn kho hiện tại!");
        }

        item.setSoLuong(newQty);
        hangHoaRepository.save(item);
    }
}