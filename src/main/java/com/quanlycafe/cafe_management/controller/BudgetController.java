package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.ExpenseFormDTO;
import com.quanlycafe.cafe_management.dto.ThuChiDTO;
import com.quanlycafe.cafe_management.entity.ChiTieu;
import com.quanlycafe.cafe_management.service.BudgetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
 * Version 1.0
 * Date: 09-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 09-06-2026   lthoai      Create and Format Convention
 */
@Controller
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    /**
     * Hiển thị trang quản lý ngân sách (Thu/Chi)
     *
     * @param tab      String
     * @param fromDate String
     * @param toDate   String
     * @param page     int
     * @param model    Model
     * @return String
     */
    @GetMapping("/budget")
    public String showBudget(
            @RequestParam(required = false, defaultValue = "overview") String tab,
            @RequestParam(required = false) String fromDate,
            @RequestParam(required = false) String toDate,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        model.addAttribute("currentTab", tab);
        model.addAttribute("activeTab", "budget");

        if ("overview".equals(tab)) {
            LocalDate start = (fromDate != null && !fromDate.isEmpty())
                    ? LocalDate.parse(fromDate) : LocalDate.now().minusDays(30);
            LocalDate end = (toDate != null && !toDate.isEmpty())
                    ? LocalDate.parse(toDate) : LocalDate.now();

            List<ThuChiDTO> reportList = budgetService.getThuChiReport(start, end);
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
        } else if ("expenses".equals(tab)) {
            int pageSize = 10;
            Page<ChiTieu> expensesPage = budgetService.getActiveExpensesPaged(page - 1, pageSize);

            model.addAttribute("expensesList", expensesPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", expensesPage.getTotalPages());
            model.addAttribute("totalItems", expensesPage.getTotalElements());
        }

        if (!model.containsAttribute("expenseForm")) {
            ExpenseFormDTO defaultForm = new ExpenseFormDTO();
            defaultForm.setNgayChi(LocalDate.now());
            model.addAttribute("expenseForm", defaultForm);
        }

        return "budget";
    }

    /**
     * Thêm mới một khoản chi
     *
     * @param form               ExpenseFormDTO
     * @param bindingResult      BindingResult
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/budget/add-expense")
    public String addExpense(@Valid @ModelAttribute("expenseForm") ExpenseFormDTO form,
                             BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.expenseForm", bindingResult);
            redirectAttributes.addFlashAttribute("expenseForm", form);
            redirectAttributes.addFlashAttribute("hasExpenseError", true);
            return "redirect:/budget?tab=expenses";
        }
        try {
            budgetService.addExpense(form);
            redirectAttributes.addFlashAttribute("successMsg", "Đã ghi nhận khoản chi mới!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/budget?tab=expenses";
    }

    /**
     * Cập nhật thông tin khoản chi
     *
     * @param form               ExpenseFormDTO
     * @param bindingResult      BindingResult
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/budget/edit-expense")
    public String editExpense(@Valid @ModelAttribute("expenseForm") ExpenseFormDTO form,
                              BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Dữ liệu chỉnh sửa không hợp lệ!");
            return "redirect:/budget?tab=expenses";
        }
        try {
            budgetService.editExpense(form);
            redirectAttributes.addFlashAttribute("successMsg", "Cập nhật khoản chi thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/budget?tab=expenses";
    }

    /**
     * Xóa khoản chi
     *
     * @param id                 Integer
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/budget/delete-expense")
    public String deleteExpense(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            budgetService.deleteExpense(id);
            redirectAttributes.addFlashAttribute("successMsg", "Đã hủy phiếu chi thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/budget?tab=expenses";
    }
}