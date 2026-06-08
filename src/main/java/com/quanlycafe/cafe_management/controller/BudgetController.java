package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.ExpenseFormDTO;
import com.quanlycafe.cafe_management.dto.ThuChiDTO;
import com.quanlycafe.cafe_management.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * BudgetController
 * * Version 1.1
 * * Date: 29-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 * 30-05-2026 lhthoai      Add PRG Validation & format convention
 */
@Controller
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    /**
     * Hiển thị trang báo cáo Thu - Chi
     *
     * @param fromDate String
     * @param toDate   String
     * @param model    Model
     * @return String
     */
    @GetMapping("/budget")
    public String showBudget(
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            Model model) {

        // Mặc định lấy 30 ngày gần nhất nếu chưa chọn
        LocalDate start = (fromDate != null && !fromDate.isEmpty()) ? LocalDate.parse(fromDate) : LocalDate.now().minusDays(30);
        LocalDate end = (toDate != null && !toDate.isEmpty()) ? LocalDate.parse(toDate) : LocalDate.now();

        List<ThuChiDTO> reportList = budgetService.getThuChiReport(start, end);

        // Tính tổng Thu, tổng Chi
        BigDecimal totalThu = BigDecimal.ZERO;
        BigDecimal totalChi = BigDecimal.ZERO;

        for (ThuChiDTO item : reportList) {
            totalThu = totalThu.add(item.getThu());
            totalChi = totalChi.add(item.getChi());
        }

        model.addAttribute("reportList", reportList);
        model.addAttribute("totalThu", totalThu);
        model.addAttribute("totalChi", totalChi);
        model.addAttribute("totalNet", totalThu.subtract(totalChi));
        model.addAttribute("fromDate", start);
        model.addAttribute("toDate", end);
        model.addAttribute("activeTab", "budget");

        // Khởi tạo form Thêm khoản chi với ngày mặc định là hôm nay
        if (!model.containsAttribute("expenseForm")) {
            ExpenseFormDTO defaultForm = new ExpenseFormDTO();
            defaultForm.setNgayChi(LocalDate.now());
            model.addAttribute("expenseForm", defaultForm);
        }

        return "budget";
    }

    /**
     * Thêm khoản chi
     *
     * @param form               ExpenseFormDTO
     * @param bindingResult      BindingResult
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/budget/add-expense")
    public String addExpense(@Valid @ModelAttribute("expenseForm") ExpenseFormDTO form,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.expenseForm", bindingResult);
            redirectAttributes.addFlashAttribute("expenseForm", form);
            redirectAttributes.addFlashAttribute("hasExpenseError", true);
            return "redirect:/budget";
        }

        try {
            budgetService.addExpense(form);
            redirectAttributes.addFlashAttribute("successMsg", "Đã ghi nhận khoản chi mới!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/budget";
    }
}