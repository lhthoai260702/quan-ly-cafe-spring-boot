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

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final HoaDonRepository hoaDonRepository;
    private final ChiTieuRepository chiTieuRepository;
    private final DonNhapRepository donNhapRepository;
    private final ThietBiRepository thietBiRepository;
    private final NhanVienRepository nhanVienRepository;

    public List<ThuChiDTO> getThuChiReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        Map<LocalDate, ThuChiDTO> reportMap = new TreeMap<>(Collections.reverseOrder());

        // 1. TỔNG HỢP TIỀN THU (Từ Hóa Đơn đã thanh toán và chưa xóa)
        List<HoaDon> hoaDons = hoaDonRepository.findByNgayGioTaoBetweenAndTrangThai(start, end, "Đã thanh toán");
        for (HoaDon hd : hoaDons) {
            if (hd.getFlagDelete() != null && hd.getFlagDelete() == 1) continue; // Bỏ qua hóa đơn đã hủy

            if (hd.getNgayGioTao() != null) {
                LocalDate date = hd.getNgayGioTao().toLocalDate();
                reportMap.putIfAbsent(date, new ThuChiDTO(date, BigDecimal.ZERO, BigDecimal.ZERO));
                ThuChiDTO dto = reportMap.get(date);

                // Cập nhật dùng BigDecimal. Tùy thuộc vào kiểu dữ liệu TongTien ở Entity của bạn.
                BigDecimal tongTien = hd.getTongTien() != null ? new BigDecimal(hd.getTongTien().toString()) : BigDecimal.ZERO;
                dto.setThu(dto.getThu().add(tongTien));
                dto.getDanhSachThu().add(hd);
            }
        }

        // 2. TỔNG HỢP TIỀN CHI
        // 2.1 Từ bảng Chi Tiêu (Phiếu chi thủ công)
        List<ChiTieu> chiTieus = chiTieuRepository.findByNgayChiBetween(start, end);
        for (ChiTieu ct : chiTieus) {
            if (ct.getFlagDelete() != null && ct.getFlagDelete() == 1) continue;
            if (ct.getNgayChi() != null) {
                addExpenseToReport(reportMap, ct.getNgayChi().toLocalDate(), ct.getTenKhoanChi(), ct.getSoTien(), ct.getNgayChi());
            }
        }

        // 2.2 Từ bảng Đơn Nhập (Nhập nguyên vật liệu/thiết bị)
        List<DonNhap> donNhaps = donNhapRepository.findByNgayNhapBetween(start, end);
        for (DonNhap dn : donNhaps) {
            if (dn.getFlagDelete() != null && dn.getFlagDelete() == 1) continue;
            if (dn.getNgayNhap() != null) {
                BigDecimal tongTienNhap = dn.getTongTien() != null ? new BigDecimal(dn.getTongTien().toString()) : BigDecimal.ZERO;
                addExpenseToReport(reportMap, dn.getNgayNhap().toLocalDate(), "Nhập kho (Mã đơn: " + dn.getMaDonNhap() + ")", tongTienNhap, dn.getNgayNhap());
            }
        }

        // 2.3 Từ bảng Thiết Bị (Mua sắm máy móc)
        List<ThietBi> thietBis = thietBiRepository.findByNgayMuaBetween(startDate, endDate);
        for (ThietBi tb : thietBis) {
            if (tb.getFlagDelete() != null && tb.getFlagDelete() == 1) continue;
            if (tb.getNgayMua() != null) {
                BigDecimal giaMua = tb.getDonGiaMua() != null ? new BigDecimal(tb.getDonGiaMua().toString()) : BigDecimal.ZERO;
                addExpenseToReport(reportMap, tb.getNgayMua(), "Mua thiết bị: " + tb.getTenThietBi(), giaMua, tb.getNgayMua().atStartOfDay());
            }
        }

        // 2.4 Trả Lương Nhân Viên (Cố định ngày 15 hàng tháng)
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
            // Duyệt từng tháng trong khoảng thời gian người dùng lọc
            LocalDate currentMonth = startDate.withDayOfMonth(1);
            while (!currentMonth.isAfter(endDate)) {
                LocalDate payday = currentMonth.withDayOfMonth(15);
                // Nếu ngày 15 nằm trong khoảng StartDate -> EndDate thì cộng tiền lương
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
     */
    private void addExpenseToReport(Map<LocalDate, ThuChiDTO> reportMap, LocalDate date, String reason, BigDecimal amount, LocalDateTime time) {
        if (amount == null) amount = BigDecimal.ZERO;
        reportMap.putIfAbsent(date, new ThuChiDTO(date, BigDecimal.ZERO, BigDecimal.ZERO));
        ThuChiDTO dto = reportMap.get(date);
        dto.setChi(dto.getChi().add(amount));
        dto.getDanhSachChi().add(new KhoanChiDTO(reason, amount, time));
    }

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
     * @return List<ChiTieu>
     */
    public List<ChiTieu> getAllActiveExpenses() {
        return chiTieuRepository.findByFlagDeleteOrderByNgayChiDesc(0);
    }

    /**
     * Sửa khoản chi
     *
     * @param form ExpenseFormDTO
     */
    @Transactional
    public void editExpense(ExpenseFormDTO form) {
        ChiTieu ct = chiTieuRepository.findByMaChiTieuAndFlagDelete(form.getId(), 0)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khoản chi hoặc đã bị xóa!"));

        ct.setTenKhoanChi(form.getTenKhoanChi());
        ct.setSoTien(BigDecimal.valueOf(form.getSoTien()));
        // Cập nhật ngày nhưng giữ nguyên giờ cũ nếu có
        LocalTime oldTime = ct.getNgayChi() != null ? ct.getNgayChi().toLocalTime() : LocalTime.now();
        ct.setNgayChi(form.getNgayChi().atTime(oldTime));

        chiTieuRepository.save(ct);
    }

    /**
     * Xóa mềm khoản chi
     *
     * @param id Integer
     */
    @Transactional
    public void deleteExpense(Integer id) {
        ChiTieu ct = chiTieuRepository.findByMaChiTieuAndFlagDelete(id, 0)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khoản chi hoặc đã bị xóa!"));
        ct.setFlagDelete(1); // Xóa mềm
        chiTieuRepository.save(ct);
    }

    /**
     * Lấy danh sách phiếu chi có phân trang
     *
     * @param page Số trang hiện tại
     * @param size Số bản ghi trên mỗi trang
     * @return Page<ChiTieu>
     */
    public Page<ChiTieu> getActiveExpensesPaged(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return chiTieuRepository.findByFlagDeleteOrderByNgayChiDesc(0, pageable);
    }
}