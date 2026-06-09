package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.PromotionFormDTO;
import com.quanlycafe.cafe_management.entity.KhuyenMai;
import com.quanlycafe.cafe_management.service.MarketingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * MarketingController
 * Version 1.3
 * Date: 09-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 29-05-2026   lhthoai     Create
 * 06-06-2026   lhthoai     Refactor: Sort by StartDate, Add TotalItems, Pagination 10
 * 09-06-2026   lhthoai     Apply Java Coding Convention
 */
@Controller
@RequiredArgsConstructor
public class MarketingController {

    private final MarketingService marketingService;

    /**
     * Hiển thị trang quản lý marketing (có phân trang và tìm kiếm)
     *
     * @param keyword String
     * @param page    int
     * @param model   Model
     * @return String
     */
    @GetMapping("/marketing")
    public String showMarketing(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "ngayBatDau"));
        Page<KhuyenMai> promoPage;

        if (!keyword.trim().isEmpty()) {
            promoPage = marketingService.searchPromotions(keyword.trim(), pageable);
        } else {
            promoPage = marketingService.getAllPromotions(pageable);
        }

        model.addAttribute("promotions", promoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", promoPage.getTotalPages());
        model.addAttribute("totalItems", promoPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeTab", "marketing");

        if (!model.containsAttribute("addForm")) {
            model.addAttribute("addForm", new PromotionFormDTO());
        }
        if (!model.containsAttribute("editForm")) {
            model.addAttribute("editForm", new PromotionFormDTO());
        }

        return "marketing";
    }

    /**
     * Thêm mới chương trình khuyến mãi
     *
     * @param form               PromotionFormDTO
     * @param bindingResult      BindingResult
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/marketing/add")
    public String addPromotion(@Valid @ModelAttribute("addForm") PromotionFormDTO form,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("hasAddError", true);
            return "redirect:/marketing";
        }
        try {
            marketingService.createPromotion(form);
            redirectAttributes.addFlashAttribute("successMsg", "Tạo chương trình thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/marketing";
    }

    /**
     * Cập nhật chương trình khuyến mãi
     *
     * @param form               PromotionFormDTO
     * @param bindingResult      BindingResult
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/marketing/edit")
    public String editPromotion(@Valid @ModelAttribute("editForm") PromotionFormDTO form,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("hasEditError", true);
            return "redirect:/marketing";
        }
        try {
            marketingService.updatePromotion(form);
            redirectAttributes.addFlashAttribute("successMsg", "Cập nhật thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/marketing";
    }

    /**
     * Xóa chương trình khuyến mãi
     *
     * @param maKhuyenMai        Integer
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/marketing/delete")
    public String deletePromotion(@RequestParam Integer maKhuyenMai,
                                  RedirectAttributes redirectAttributes) {
        try {
            marketingService.deletePromotion(maKhuyenMai);
            redirectAttributes.addFlashAttribute("successMsg", "Đã xóa chương trình!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/marketing";
    }
}