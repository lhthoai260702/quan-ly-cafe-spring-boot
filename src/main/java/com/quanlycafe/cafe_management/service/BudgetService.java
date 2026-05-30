package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.dto.ExpenseFormDTO;
import com.quanlycafe.cafe_management.dto.ThuChiDTO;
import com.quanlycafe.cafe_management.entity.ChiTieu;
import com.quanlycafe.cafe_management.entity.HoaDon;
import com.quanlycafe.cafe_management.repository.ChiTieuRepository;
import com.quanlycafe.cafe_management.repository.HoaDonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * BudgetService
 * * Version 1.1
 * * Date: 29-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 30-05-2026 Quản Lý      Apply ExpenseFormDTO & format convention
 */
@Service
@RequiredArgsConstructor
public class BudgetService {

    private final HoaDonRepository hoaDonRepository;
    private final ChiTieuRepository chiTieuRepository;

    /**
     * Lấy báo cáo Thu - Chi
     *
     * @param startDate LocalDate
     * @param endDate   LocalDate
     * @return List<ThuChiDTO>
     */
    public List<ThuChiDTO> getThuChiReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        Map<LocalDate, ThuChiDTO> reportMap = new TreeMap<>(Collections.reverseOrder());

        // 1. Lấy dữ liệu Thu
        List<HoaDon> hoaDons = hoaDonRepository.findByNgayGioTaoBetweenAndTrangThai(start, end, "Đã thanh toán");
        for (HoaDon hd : hoaDons) {
            if (hd.getNgayGioTao() != null) {
                LocalDate date = hd.getNgayGioTao().toLocalDate();
                reportMap.putIfAbsent(date, new ThuChiDTO(date, BigDecimal.ZERO, BigDecimal.ZERO));

                ThuChiDTO dto = reportMap.get(date);
                BigDecimal currentThu = dto.getThu();

                dto.setThu(currentThu.add(hd.getTongTien() != null ? BigDecimal.valueOf(hd.getTongTien()) : BigDecimal.ZERO));
                dto.getDanhSachThu().add(hd);
            }
        }

        // 2. Lấy dữ liệu Chi
        List<ChiTieu> chiTieus = chiTieuRepository.findByNgayChiBetween(start, end);
        for (ChiTieu ct : chiTieus) {
            if (ct.getNgayChi() != null) {
                LocalDate date = ct.getNgayChi().toLocalDate();
                reportMap.putIfAbsent(date, new ThuChiDTO(date, BigDecimal.ZERO, BigDecimal.ZERO));

                ThuChiDTO dto = reportMap.get(date);
                BigDecimal currentChi = dto.getChi();

                dto.setChi(currentChi.add(ct.getSoTien() != null ? ct.getSoTien() : BigDecimal.ZERO));
                dto.getDanhSachChi().add(ct);
            }
        }

        return new ArrayList<>(reportMap.values());
    }

    /**
     * Thêm khoản chi
     *
     * @param form ExpenseFormDTO
     */
    @Transactional
    public void addExpense(ExpenseFormDTO form) {
        ChiTieu ct = new ChiTieu();
        ct.setTenKhoanChi(form.getTenKhoanChi());
        ct.setSoTien(BigDecimal.valueOf(form.getSoTien()));
        ct.setNgayChi(form.getNgayChi().atTime(LocalTime.now()));

        chiTieuRepository.save(ct);
    }
}