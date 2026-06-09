package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.dto.ExpenseFormDTO;
import com.quanlycafe.cafe_management.dto.KhoanChiDTO;
import com.quanlycafe.cafe_management.dto.ThuChiDTO;
import com.quanlycafe.cafe_management.entity.*;
import com.quanlycafe.cafe_management.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * BudgetService
 * Version 1.2
 * Date: 09-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 29-05-2026   lhthoai     Create
 * 09-06-2026   Quản Lý     Apply Java Coding Convention & Add Javadoc
 */
@Service
@RequiredArgsConstructor
public class BudgetService {

    private final HoaDonRepository hoaDonRepository;
    private final ChiTieuRepository chiTieuRepository;
    private final DonNhapRepository donNhapRepository;
    private final ThietBiRepository thietBiRepository;
    private final NhanVienRepository nhanVienRepository;

    /**
     * Lấy báo cáo Thu/Chi theo khoảng thời gian
     *
     * @param startDate Ngày bắt đầu
     * @param endDate   Ngày kết thúc
     * @return Danh sách các DTO Thu Chi
     */
    public List<ThuChiDTO> getThuChiReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        Map<LocalDate, ThuChiDTO> reportMap = new TreeMap<>(Collections.reverseOrder());

        // 1. Tổng hợp tiền thu
        List<HoaDon> hoaDons = hoaDonRepository.findByNgayGioTaoBetweenAndTrangThai(start, end, "Đã thanh toán");
        for (HoaDon hd : hoaDons) {
            if (hd.getFlagDelete() != null && hd.getFlagDelete() == 1) {
                continue;
            }

            if (hd.getNgayGioTao() != null) {
                LocalDate date = hd.getNgayGioTao().toLocalDate();
                reportMap.putIfAbsent(date, new ThuChiDTO(date, BigDecimal.ZERO, BigDecimal.ZERO));
                ThuChiDTO dto = reportMap.get(date);

                BigDecimal tongTien = hd.getTongTien() != null ? new BigDecimal(hd.getTongTien().toString()) : BigDecimal.ZERO;
                dto.setThu(dto.getThu().add(tongTien));
                dto.getDanhSachThu().add(hd);
            }
        }

        // 2. Tổng hợp tiền chi
        List<ChiTieu> chiTieus = chiTieuRepository.findByNgayChiBetween(start, end);
        for (ChiTieu ct : chiTieus) {
            if (ct.getFlagDelete() != null && ct.getFlagDelete() == 1) {
                continue;
            }
            if (ct.getNgayChi() != null) {
                addExpenseToReport(reportMap, ct.getNgayChi().toLocalDate(), ct.getTenKhoanChi(), ct.getSoTien(), ct.getNgayChi());
            }
        }

        List<DonNhap> donNhaps = donNhapRepository.findByNgayNhapBetween(start, end);
        for (DonNhap dn : donNhaps) {
            if (dn.getFlagDelete() != null && dn.getFlagDelete() == 1) {
                continue;
            }
            if (dn.getNgayNhap() != null) {
                BigDecimal tongTienNhap = dn.getTongTien() != null ? new BigDecimal(dn.getTongTien().toString()) : BigDecimal.ZERO;
                addExpenseToReport(reportMap, dn.getNgayNhap().toLocalDate(), "Nhập kho (Mã đơn: " + dn.getMaDonNhap() + ")", tongTienNhap, dn.getNgayNhap());
            }
        }

        List<ThietBi> thietBis = thietBiRepository.findByNgayMuaBetween(startDate, endDate);
        for (ThietBi tb : thietBis) {
            if (tb.getFlagDelete() != null && tb.getFlagDelete() == 1) {
                continue;
            }
            if (tb.getNgayMua() != null) {
                BigDecimal giaMua = tb.getDonGiaMua() != null ? new BigDecimal(tb.getDonGiaMua().toString()) : BigDecimal.ZERO;
                addExpenseToReport(reportMap, tb.getNgayMua(), "Mua thiết bị: " + tb.getTenThietBi(), giaMua, tb.getNgayMua().atStartOfDay());
            }
        }

        // 3. Trả Lương Nhân Viên
        List<NhanVien> activeEmployees = nhanVienRepository.findAll();
        BigDecimal totalSalary = BigDecimal.ZERO;
        for (NhanVien nv : activeEmployees) {
            if (nv.getFlagDelete() == null || nv.getFlagDelete() == 0) {
                if (nv.getLuong() != null) {
                    totalSalary = totalSalary.add(new BigDecimal(nv.getLuong().toString()));
                }
            }
        }

        if (totalSalary.compareTo(BigDecimal.ZERO) > 0) {
            LocalDate currentMonth = startDate.withDayOfMonth(1);
            while (!currentMonth.isAfter(endDate)) {
                LocalDate payday = currentMonth.withDayOfMonth(15);
                if (!payday.isBefore(startDate) && !payday.isAfter(endDate)) {
                    addExpenseToReport(reportMap, payday, "Trả lương nhân viên tháng " + currentMonth.getMonthValue(), totalSalary, payday.atTime(8, 0));
                }
                currentMonth = currentMonth.plusMonths(1);
            }
        }

        return new ArrayList<>(reportMap.values());
    }

    /**
     * Hàm hỗ trợ nạp tiền chi vào Map
     *
     * @param reportMap Map báo cáo
     * @param date      Ngày chi
     * @param reason    Lý do chi
     * @param amount    Số tiền
     * @param time      Thời gian chi
     */
    private void addExpenseToReport(Map<LocalDate, ThuChiDTO> reportMap, LocalDate date, String reason, BigDecimal amount, LocalDateTime time) {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        reportMap.putIfAbsent(date, new ThuChiDTO(date, BigDecimal.ZERO, BigDecimal.ZERO));
        ThuChiDTO dto = reportMap.get(date);
        dto.setChi(dto.getChi().add(amount));
        dto.getDanhSachChi().add(new KhoanChiDTO(reason, amount, time));
    }

    /**
     * Thêm mới một khoản chi thủ công
     *
     * @param form Form dữ liệu khoản chi
     */
    @Transactional
    public void addExpense(ExpenseFormDTO form) {
        ChiTieu ct = new ChiTieu();
        ct.setTenKhoanChi(form.getTenKhoanChi());
        ct.setSoTien(BigDecimal.valueOf(form.getSoTien()));
        ct.setNgayChi(form.getNgayChi().atTime(LocalTime.now()));
        ct.setFlagDelete(0);
        chiTieuRepository.save(ct);
    }

    /**
     * Lấy danh sách toàn bộ phiếu chi thủ công (chưa bị xóa)
     *
     * @return Danh sách khoản chi
     */
    public List<ChiTieu> getAllActiveExpenses() {
        return chiTieuRepository.findByFlagDeleteOrderByNgayChiDesc(0);
    }

    /**
     * Sửa khoản chi
     *
     * @param form Form dữ liệu khoản chi
     */
    @Transactional
    public void editExpense(ExpenseFormDTO form) {
        ChiTieu ct = chiTieuRepository.findByMaChiTieuAndFlagDelete(form.getId(), 0)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khoản chi hoặc đã bị xóa!"));

        ct.setTenKhoanChi(form.getTenKhoanChi());
        ct.setSoTien(BigDecimal.valueOf(form.getSoTien()));
        LocalTime oldTime = ct.getNgayChi() != null ? ct.getNgayChi().toLocalTime() : LocalTime.now();
        ct.setNgayChi(form.getNgayChi().atTime(oldTime));

        chiTieuRepository.save(ct);
    }

    /**
     * Xóa mềm khoản chi
     *
     * @param id Mã khoản chi cần xóa
     */
    @Transactional
    public void deleteExpense(Integer id) {
        ChiTieu ct = chiTieuRepository.findByMaChiTieuAndFlagDelete(id, 0)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khoản chi hoặc đã bị xóa!"));
        ct.setFlagDelete(1);
        chiTieuRepository.save(ct);
    }

    /**
     * Lấy danh sách phiếu chi có phân trang
     *
     * @param page Số trang
     * @param size Số bản ghi mỗi trang
     * @return Trang kết quả
     */
    public Page<ChiTieu> getActiveExpensesPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return chiTieuRepository.findByFlagDeleteOrderByNgayChiDesc(0, pageable);
    }
}