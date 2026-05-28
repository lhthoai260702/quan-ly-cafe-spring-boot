package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.ThuChiDTO;
import com.quanlycafe.cafe_management.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping("/budget")
    public String showBudget(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            Model model) {

        // Mặc định lấy 30 ngày gần nhất nếu chưa chọn
        LocalDate start = (fromDate != null && !fromDate.isEmpty()) ? LocalDate.parse(fromDate) : LocalDate.now().minusDays(30);
        LocalDate end = (toDate != null && !toDate.isEmpty()) ? LocalDate.parse(toDate) : LocalDate.now();

        List<ThuChiDTO> reportList = budgetService.getThuChiReport(start, end);

        // Tính tổng Thu, tổng Chi dưới DB lên
        BigDecimal totalThu = BigDecimal.ZERO;
        BigDecimal totalChi = BigDecimal.ZERO;

        for (ThuChiDTO item : reportList) {
            totalThu = totalThu.add(item.getThu());
            totalChi = totalChi.add(item.getChi());
        }

        model.addAttribute("reportList", reportList);
        model.addAttribute("totalThu", totalThu);
        model.addAttribute("totalChi", totalChi);
        model.addAttribute("totalNet", totalThu.subtract(totalChi)); // Lợi nhuận
        model.addAttribute("fromDate", start);
        model.addAttribute("toDate", end);
        model.addAttribute("activeTab", "budget");

        return "budget";
    }

    @PostMapping("/budget/add-expense")
    public String addExpense(
            @RequestParam String tenKhoanChi,
            @RequestParam Double soTien,
            @RequestParam String ngayChi) {
        try {
            budgetService.addExpense(tenKhoanChi, soTien, LocalDate.parse(ngayChi));
            return "redirect:/budget?success=add";
        } catch (Exception e) {
            return "redirect:/budget?error=add";
        }
    }
}