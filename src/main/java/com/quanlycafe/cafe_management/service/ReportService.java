package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.entity.ChiTieu;
import com.quanlycafe.cafe_management.entity.HoaDon;
import com.quanlycafe.cafe_management.repository.ChiTieuRepository;
import com.quanlycafe.cafe_management.repository.HoaDonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final HoaDonRepository hoaDonRepository;
    private final ChiTieuRepository chiTieuRepository; // Gọi thêm kho lưu Chi Tiêu

    // 1. Biểu đồ Doanh thu (7 ngày)
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
                    .map(HoaDon::getTongTien)
                    .filter(Objects::nonNull)
                    .reduce(0.0, Double::sum);
            data.add(dailyTotal);
        }
        return Map.of("labels", labels, "data", data);
    }

    // 2. Biểu đồ Số lượng đơn hàng (7 ngày)
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

            // Đếm số lượng hóa đơn thay vì cộng tiền
            data.add(invoices.size());
        }
        return Map.of("labels", labels, "data", data);
    }

    // 3. Biểu đồ Món ăn bán chạy
    public Map<String, Object> getTopDishes() {
        List<String> labels = new ArrayList<>();
        List<Long> data = new ArrayList<>();

        List<Object[]> topDishes = hoaDonRepository.getTopSellingDishes();
        for (Object[] row : topDishes) {
            labels.add((String) row[0]);
            data.add(((Number) row[1]).longValue());
        }
        return Map.of("labels", labels, "data", data);
    }

    // 4. Biểu đồ Tỉ lệ Thu - Chi (Trong tháng hiện tại)
    public Map<String, Object> getIncomeExpenseCurrentMonth() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1); // Ngày đầu tháng
        LocalDateTime start = startOfMonth.atStartOfDay();
        LocalDateTime end = LocalDate.now().atTime(LocalTime.MAX);

        // Tính tổng Thu
        List<HoaDon> invoices = hoaDonRepository.findByNgayGioTaoBetweenAndTrangThai(start, end, "Đã thanh toán");
        Double totalThu = invoices.stream()
                .map(HoaDon::getTongTien)
                .filter(Objects::nonNull)
                .reduce(0.0, Double::sum);

        // Tính tổng Chi
        List<ChiTieu> expenses = chiTieuRepository.findByNgayChiBetween(start, end);
        Double totalChi = expenses.stream()
                .map(ct -> ct.getSoTien() != null ? ct.getSoTien().doubleValue() : 0.0) // Đưa BigDecimal về Double để tính
                .reduce(0.0, Double::sum);

        // Gửi mảng gồm 2 phần tử: [Tổng Thu, Tổng Chi]
        return Map.of("labels", Arrays.asList("Tổng Thu", "Tổng Chi"), "data", Arrays.asList(totalThu, totalChi));
    }
}