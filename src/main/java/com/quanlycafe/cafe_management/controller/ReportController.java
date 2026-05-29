package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * ReportController
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 */
@Controller
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * Hiển thị trang báo cáo
     *
     * @param model Model
     * @return String
     */
    @GetMapping("/report")
    public String showReport(Model model) {

        Map<String, Object> revenueData = reportService.getRevenueLast7Days();
        model.addAttribute("revenueLabels", revenueData.get("labels"));
        model.addAttribute("revenueValues", revenueData.get("data"));

        Map<String, Object> dishData = reportService.getTopDishes();
        model.addAttribute("dishLabels", dishData.get("labels"));
        model.addAttribute("dishValues", dishData.get("data"));

        // THÊM: Dữ liệu Đơn hàng
        Map<String, Object> orderData = reportService.getOrderCountLast7Days();
        model.addAttribute("orderLabels", orderData.get("labels"));
        model.addAttribute("orderValues", orderData.get("data"));

        // THÊM: Dữ liệu Thu/Chi
        Map<String, Object> financeData = reportService.getIncomeExpenseCurrentMonth();
        model.addAttribute("financeLabels", financeData.get("labels"));
        model.addAttribute("financeValues", financeData.get("data"));

        model.addAttribute("activeTab", "report");

        return "report";
    }
}