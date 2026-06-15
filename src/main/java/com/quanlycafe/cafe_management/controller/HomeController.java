package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.entity.HoaDon;
import com.quanlycafe.cafe_management.repository.BanRepository;
import com.quanlycafe.cafe_management.repository.HoaDonRepository;
import com.quanlycafe.cafe_management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * HomeController
 * Version 1.2
 * Date: 15-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 29-05-2026   lhthoai     Create
 * 09-06-2026   lhthoai     Thêm logic thống kê dữ liệu thật cho Dashboard và Format Convention
 * 15-06-2026   lhthoai     Cập nhật Món bán chạy nhất (Tháng này) & Lọc hóa đơn đã hủy
 */
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final HoaDonRepository hoaDonRepository;
    private final BanRepository banRepository;
    private final ReportService reportService;

    /**
     * Hiển thị trang chủ (Dashboard) với các thông số thống kê trong ngày
     *
     * @param model Model
     * @return String
     */
    @GetMapping({"/", "/home"})
    public String home(Model model) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

        // 🚀 Lấy thêm mốc thời gian đầu tháng
        LocalDateTime startOfMonth = today.withDayOfMonth(1).atStartOfDay();

        // 1. Tính Doanh thu & Số đơn hàng trong ngày hôm nay
        List<HoaDon> hoaDonsToday = hoaDonRepository.findByNgayGioTaoBetweenAndTrangThai(
                startOfDay, endOfDay, "Đã thanh toán");

        // 🚀 Lọc bỏ các hóa đơn đã hủy khi tính doanh thu
        Double doanhThuHomNay = hoaDonsToday.stream()
                .filter(hd -> hd.getFlagDelete() == null || hd.getFlagDelete() == 0)
                .map(HoaDon::getTongTien)
                .filter(Objects::nonNull)
                .reduce(0.0, Double::sum);

        // 🚀 Lọc bỏ các hóa đơn đã hủy khi đếm số đơn
        long donHangHomNay = hoaDonsToday.stream()
                .filter(hd -> hd.getFlagDelete() == null || hd.getFlagDelete() == 0)
                .count();

        // 2. Số bàn đang có khách (Đang phục vụ)
        long banDangSuDung = banRepository.countByTinhTrang("Đang sử dụng");

        // 3. Món bán chạy nhất (Tháng này) - Đồng bộ với ReportService
        String monBanChay = "Chưa có dữ liệu";
        List<Object[]> topDishes = hoaDonRepository.getTopSellingDishesCurrentMonth(startOfMonth, LocalDateTime.now());

        if (topDishes != null && !topDishes.isEmpty()) {
            monBanChay = (String) topDishes.get(0)[0];
        }

        // 4. Dữ liệu cho biểu đồ Doanh thu 7 ngày gần nhất
        Map<String, Object> revenueData = reportService.getRevenueLast7Days();

        // 5. Hoạt động gần đây (Lấy 5 hóa đơn thanh toán mới nhất, bỏ qua hóa đơn hủy)
        List<HoaDon> hoatDongGanDay = new ArrayList<>();
        for (HoaDon hd : hoaDonsToday) {
            if (hd.getFlagDelete() == null || hd.getFlagDelete() == 0) {
                hoatDongGanDay.add(hd);
            }
        }
        Collections.reverse(hoatDongGanDay); // Đảo ngược để lấy mới nhất lên đầu

        if (hoatDongGanDay.size() > 5) {
            hoatDongGanDay = hoatDongGanDay.subList(0, 5);
        }

        // Đẩy dữ liệu lên Model
        model.addAttribute("doanhThuHomNay", doanhThuHomNay);
        model.addAttribute("donHangHomNay", donHangHomNay); // Truyền long thay vì int
        model.addAttribute("banDangSuDung", banDangSuDung);
        model.addAttribute("monBanChay", monBanChay);

        model.addAttribute("chartLabels", revenueData.get("labels"));
        model.addAttribute("chartData", revenueData.get("data"));
        model.addAttribute("hoatDongGanDay", hoatDongGanDay);

        return "home";
    }
}