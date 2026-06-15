package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.dto.ThuChiDTO;
import com.quanlycafe.cafe_management.entity.HoaDon;
import com.quanlycafe.cafe_management.repository.HoaDonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ReportService
 * Version 1.2
 * Date: 15-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 29-05-2026   lhthoai     Create
 * 09-06-2026   lhthoai     Apply Java Coding Convention & Add Javadoc
 * 15-06-2026   Quản Lý     Đồng bộ logic Thu - Chi với BudgetService và lọc Hóa đơn đã hủy
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final HoaDonRepository hoaDonRepository;

    // Xóa ChiTieuRepository và tiêm thẳng BudgetService vào đây
    private final BudgetService budgetService;

    /**
     * Lấy dữ liệu doanh thu trong 7 ngày gần nhất để vẽ biểu đồ.
     */
    public Map<String, Object> getRevenueLast7Days() {
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 6; i >= 0; i--) {
            LocalDate targetDate = today.minusDays(i);
            labels.add(targetDate.format(formatter));

            LocalDateTime startOfDay = targetDate.atStartOfDay();
            LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);
            List<HoaDon> invoices = hoaDonRepository.findByNgayGioTaoBetweenAndTrangThai(startOfDay, endOfDay, "Đã thanh toán");

            Double dailyTotal = invoices.stream()
                    .filter(hd -> hd.getFlagDelete() == null || hd.getFlagDelete() == 0) // Lọc bỏ hóa đơn đã hủy
                    .map(HoaDon::getTongTien)
                    .filter(Objects::nonNull)
                    .reduce(0.0, Double::sum);
            data.add(dailyTotal);
        }

        return Map.of("labels", labels, "data", data);
    }

    /**
     * Lấy dữ liệu số lượng đơn hàng trong 7 ngày gần nhất để vẽ biểu đồ.
     */
    public Map<String, Object> getOrderCountLast7Days() {
        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");

        for (int i = 6; i >= 0; i--) {
            LocalDate targetDate = today.minusDays(i);
            labels.add(targetDate.format(formatter));

            LocalDateTime startOfDay = targetDate.atStartOfDay();
            LocalDateTime endOfDay = targetDate.atTime(LocalTime.MAX);
            List<HoaDon> invoices = hoaDonRepository.findByNgayGioTaoBetweenAndTrangThai(startOfDay, endOfDay, "Đã thanh toán");

            long count = invoices.stream()
                    .filter(hd -> hd.getFlagDelete() == null || hd.getFlagDelete() == 0) // Lọc bỏ hóa đơn đã hủy
                    .count();
            data.add((int) count);
        }

        return Map.of("labels", labels, "data", data);
    }

    /**
     * Lấy danh sách các món ăn bán chạy nhất (Tháng này).
     */
    public Map<String, Object> getTopDishes() {
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        // Lấy ngày đầu tiên của tháng hiện tại và thời điểm hiện tại
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime now = LocalDateTime.now();

        // Gọi hàm mới đã có lọc ngày
        List<Object[]> topDishes = hoaDonRepository.getTopSellingDishesCurrentMonth(startOfMonth, now);

        for (Object[] row : topDishes) {
            labels.add((String) row[0]);
            data.add(((Number) row[1]).longValue());
        }

        return Map.of("labels", labels, "data", data);
    }

    /**
     * Lấy dữ liệu báo cáo Thu - Chi của tháng hiện tại.
     * Đã được đồng bộ để lấy dữ liệu trực tiếp từ BudgetService.
     */
    public Map<String, Object> getIncomeExpenseCurrentMonth() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate today = LocalDate.now();

        // 🚀 Gọi trực tiếp hàm getThuChiReport từ BudgetService
        List<ThuChiDTO> reportList = budgetService.getThuChiReport(startOfMonth, today);

        BigDecimal totalThu = BigDecimal.ZERO;
        BigDecimal totalChi = BigDecimal.ZERO;

        // Cộng dồn toàn bộ báo cáo trả về
        for (ThuChiDTO dto : reportList) {
            if (dto.getThu() != null) {
                totalThu = totalThu.add(dto.getThu());
            }
            if (dto.getChi() != null) {
                totalChi = totalChi.add(dto.getChi());
            }
        }

        return Map.of(
                "labels", Arrays.asList("Tổng Thu", "Tổng Chi"),
                "data", Arrays.asList(totalThu.doubleValue(), totalChi.doubleValue())
        );
    }
}