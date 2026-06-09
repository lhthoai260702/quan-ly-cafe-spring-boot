package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.dto.*;
import com.quanlycafe.cafe_management.entity.DonNhap;
import com.quanlycafe.cafe_management.entity.DonViTinh;
import com.quanlycafe.cafe_management.entity.HangHoa;
import com.quanlycafe.cafe_management.repository.DonNhapRepository;
import com.quanlycafe.cafe_management.repository.DonViTinhRepository;
import com.quanlycafe.cafe_management.repository.HangHoaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * InventoryService
 * Version 1.5
 * Date: 09-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 29-05-2026   lhthoai     Create
 * 30-05-2026   lhthoai     Apply DTOs, format convention & Pagination
 * 08-06-2026   lhthoai     Remove Export logic, Integrate DonNhap, Add Filter, Fix Sorting
 * 09-06-2026   lhthoai     Apply Java Coding Convention & Add Javadoc
 */
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final HangHoaRepository hangHoaRepository;
    private final DonViTinhRepository donViTinhRepository;
    private final DonNhapRepository donNhapRepository;

    /**
     * Tìm kiếm và lọc hàng hóa theo tên và đơn vị tính.
     *
     * @param keyword  Từ khóa tìm kiếm
     * @param unitId   Mã đơn vị tính (0 = Tất cả)
     * @param pageable Đối tượng phân trang
     * @return Trang chứa danh sách InventoryItemDTO
     */
    public Page<InventoryItemDTO> searchAndFilterItems(String keyword, Integer unitId, Pageable pageable) {
        Page<HangHoa> page;
        if (unitId != null && unitId > 0) {
            page = hangHoaRepository.findByTenHangHoaContainingIgnoreCaseAndDonViTinh_MaDonViTinh(keyword, unitId, pageable);
        } else {
            page = hangHoaRepository.findByTenHangHoaContainingIgnoreCase(keyword, pageable);
        }

        return page.map(hh -> {
            InventoryItemDTO dto = new InventoryItemDTO();
            dto.setMaHangHoa(hh.getMaHangHoa());
            dto.setTenHangHoa(hh.getTenHangHoa());
            dto.setSoLuong(hh.getSoLuong());
            dto.setDonGia(hh.getDonGia());
            dto.setDonViTinh(hh.getDonViTinh());

            List<DonNhap> listDonNhap = donNhapRepository.findByHangHoa_MaHangHoaAndFlagDeleteOrderByNgayNhapDesc(hh.getMaHangHoa(), 0);
            BigDecimal tongGiaTri = listDonNhap.stream()
                    .map(DonNhap::getTongTien)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            dto.setTongGiaTri(tongGiaTri);

            return dto;
        });
    }

    /**
     * Lấy danh sách tất cả các đơn vị tính.
     *
     * @return Danh sách Đơn vị tính
     */
    public List<DonViTinh> getAllUnits() {
        return donViTinhRepository.findAll();
    }

    /**
     * Tạo mới một mặt hàng trong kho.
     *
     * @param form Dữ liệu form nhập hàng
     */
    @Transactional
    public void createItem(InventoryFormDTO form) {
        DonViTinh unit = donViTinhRepository.findById(form.getMaDonViTinh()).orElse(null);
        HangHoa item = new HangHoa();
        item.setTenHangHoa(form.getTenHangHoa());
        item.setSoLuong(form.getSoLuong() != null ? BigDecimal.valueOf(form.getSoLuong()) : BigDecimal.ZERO);
        item.setDonViTinh(unit);
        item.setDonGia(BigDecimal.valueOf(form.getDonGia()));

        HangHoa savedItem = hangHoaRepository.save(item);

        if (savedItem.getSoLuong().compareTo(BigDecimal.ZERO) > 0) {
            DonNhap donNhap = new DonNhap();
            donNhap.setHangHoa(savedItem);
            donNhap.setSoLuong(savedItem.getSoLuong());
            donNhap.setTongTien(savedItem.getSoLuong().multiply(savedItem.getDonGia()));
            donNhap.setNgayNhap(form.getNgayNhap() != null ? form.getNgayNhap().atTime(LocalTime.now()) : LocalDateTime.now());
            donNhapRepository.save(donNhap);
        }
    }

    /**
     * Cập nhật thông tin mặt hàng.
     *
     * @param form Dữ liệu form cập nhật
     */
    @Transactional
    public void updateItem(InventoryFormDTO form) {
        HangHoa item = hangHoaRepository.findById(form.getMaHangHoa()).orElseThrow();
        DonViTinh unit = donViTinhRepository.findById(form.getMaDonViTinh()).orElse(null);

        item.setTenHangHoa(form.getTenHangHoa());
        item.setDonViTinh(unit);
        hangHoaRepository.save(item);
    }

    /**
     * Xóa mềm một mặt hàng trong kho (Đổi cờ flag_delete = 1).
     *
     * @param maHangHoa Mã hàng hóa cần xóa
     */
    @Transactional
    public void deleteItem(Integer maHangHoa) {
        HangHoa item = hangHoaRepository.findById(maHangHoa)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hàng hóa!"));

        item.setFlagDelete(1);
        item.setSoLuong(BigDecimal.ZERO);
        hangHoaRepository.save(item);
    }

    /**
     * Thực hiện nhập thêm hàng vào kho.
     *
     * @param form Dữ liệu form nhập hàng
     */
    @Transactional
    public void importStock(ImportStockDTO form) {
        HangHoa item = hangHoaRepository.findById(form.getMaHangHoa()).orElseThrow();

        BigDecimal qtyToAdd = BigDecimal.valueOf(form.getSoLuongThaoTac());
        BigDecimal donGiaNhap = BigDecimal.valueOf(form.getDonGia());
        BigDecimal currentQty = item.getSoLuong() != null ? item.getSoLuong() : BigDecimal.ZERO;

        item.setSoLuong(currentQty.add(qtyToAdd));
        item.setDonGia(donGiaNhap);
        hangHoaRepository.save(item);

        DonNhap donNhap = new DonNhap();
        donNhap.setHangHoa(item);
        donNhap.setSoLuong(qtyToAdd);
        donNhap.setTongTien(qtyToAdd.multiply(donGiaNhap));
        donNhap.setNgayNhap(form.getNgayNhap() != null ? form.getNgayNhap().atTime(LocalTime.now()) : LocalDateTime.now());
        donNhapRepository.save(donNhap);
    }

    /**
     * Lấy lịch sử nhập hàng của một mặt hàng.
     *
     * @param maHangHoa Mã hàng hóa
     * @return Danh sách lịch sử nhập hàng
     */
    public List<DonNhapHistoryDTO> getImportHistory(Integer maHangHoa) {
        List<DonNhap> list = donNhapRepository.findByHangHoa_MaHangHoaAndFlagDeleteOrderByNgayNhapDesc(maHangHoa, 0);
        return list.stream().map(dn -> {
            DonNhapHistoryDTO dto = new DonNhapHistoryDTO();
            dto.setMaDonNhap(dn.getMaDonNhap());
            dto.setNgayNhap(dn.getNgayNhap());
            dto.setSoLuong(dn.getSoLuong());
            dto.setTongTien(dn.getTongTien());

            if (dn.getSoLuong() != null && dn.getSoLuong().compareTo(BigDecimal.ZERO) > 0) {
                dto.setDonGia(dn.getTongTien().divide(dn.getSoLuong(), 2, RoundingMode.HALF_UP));
            } else {
                dto.setDonGia(BigDecimal.ZERO);
            }
            return dto;
        }).toList();
    }

    /**
     * Cập nhật thông tin lịch sử đơn nhập.
     *
     * @param form Dữ liệu đơn nhập cần chỉnh sửa
     */
    @Transactional
    public void updateDonNhapHistory(DonNhapEditDTO form) {
        DonNhap dn = donNhapRepository.findById(form.getMaDonNhap())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn nhập"));
        HangHoa hh = dn.getHangHoa();

        BigDecimal oldQty = dn.getSoLuong();
        BigDecimal newQty = BigDecimal.valueOf(form.getSoLuong());
        BigDecimal diff = newQty.subtract(oldQty);

        hh.setSoLuong(hh.getSoLuong().add(diff));
        hangHoaRepository.save(hh);

        dn.setNgayNhap(form.getNgayNhap().atTime(dn.getNgayNhap().toLocalTime()));
        dn.setSoLuong(newQty);
        dn.setTongTien(newQty.multiply(BigDecimal.valueOf(form.getDonGia())));
        donNhapRepository.save(dn);
    }

    /**
     * Xóa mềm lịch sử đơn nhập.
     *
     * @param maDonNhap Mã đơn nhập cần xóa
     */
    @Transactional
    public void deleteDonNhapHistory(Integer maDonNhap) {
        DonNhap dn = donNhapRepository.findById(maDonNhap)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn nhập"));

        HangHoa hh = dn.getHangHoa();

        BigDecimal currentQty = hh.getSoLuong() != null ? hh.getSoLuong() : BigDecimal.ZERO;
        hh.setSoLuong(currentQty.subtract(dn.getSoLuong()));
        hangHoaRepository.save(hh);

        dn.setFlagDelete(1);
        donNhapRepository.save(dn);
    }
}