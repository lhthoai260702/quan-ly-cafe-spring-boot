package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.entity.ChiTieu;
import com.quanlycafe.cafe_management.entity.HoaDon;
import com.quanlycafe.cafe_management.repository.ChiTieuRepository;
import com.quanlycafe.cafe_management.repository.HoaDonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * ReportService
 * Version 1.1
 * Date: 09-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 29-05-2026   lhthoai     Create
 * 09-06-2026   lhthoai     Apply Java Coding Convention & Add Javadoc
 */
@Service
@RequiredArgsConstructor
public class ReportService {

    private final HoaDonRepository hoaDonRepository;
    private final ChiTieuRepository chiTieuRepository;

    /**
     * Lấy dữ liệu doanh thu trong 7 ngày gần nhất để vẽ biểu đồ.
     *
     * @return Map chứa nhãn thời gian và dữ liệu doanh thu
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
                    .map(HoaDon::getTongTien)
                    .filter(Objects::nonNull)
                    .reduce(0.0, Double::sum);
            data.add(dailyTotal);
        }

        return Map.of("labels", labels, "data", data);
    }

    /**
     * Lấy dữ liệu số lượng đơn hàng trong 7 ngày gần nhất để vẽ biểu đồ.
     *
     * @return Map chứa nhãn thời gian và số lượng đơn
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

            data.add(invoices.size());
        }

        return Map.of("labels", labels, "data", data);
    }

    /**
     * Lấy danh sách các món ăn bán chạy nhất.
     *
     * @return Map chứa nhãn tên món và số lượng đã bán
     */
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

    /**
     * Lấy dữ liệu báo cáo Thu - Chi của tháng hiện tại.
     *
     * @return Map chứa tiêu đề và dữ liệu tổng hợp
     */
    public Map<String, Object> getIncomeExpenseCurrentMonth() {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
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
                .map(ct -> ct.getSoTien() != null ? ct.getSoTien().doubleValue() : 0.0)
                .reduce(0.0, Double::sum);

        return Map.of("labels", Arrays.asList("Tổng Thu", "Tổng Chi"), "data", Arrays.asList(totalThu, totalChi));
    }
}