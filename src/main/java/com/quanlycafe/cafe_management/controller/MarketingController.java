package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.PromotionFormDTO;
import com.quanlycafe.cafe_management.entity.KhuyenMai;
import com.quanlycafe.cafe_management.service.MarketingService;
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

import java.util.List;

/**
 * MarketingController
 * * Version 1.1
 * * Date: 29-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 30-05-2026 Quản Lý      Add PRG Validation & format convention
 */
@Controller
@RequiredArgsConstructor
public class MarketingController {

    private final MarketingService marketingService;

    /**
     * Hiển thị trang marketing
     *
     * @param keyword Từ khóa tìm kiếm
     * @param model   Model
     * @return String
     */
    @GetMapping("/marketing")
    public String showMarketing(
            @RequestParam(required = false) String keyword,
            Model model) {

        List<KhuyenMai> promotions;

        if (keyword != null && !keyword.trim().isEmpty()) {
            promotions = marketingService.searchPromotions(keyword.trim());
        } else {
            promotions = marketingService.getAllPromotions();
        }

        model.addAttribute("promotions", promotions);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeTab", "marketing");

        // Khởi tạo DTO rỗng để binding vào View
        if (!model.containsAttribute("addForm")) {
            model.addAttribute("addForm", new PromotionFormDTO());
        }
        if (!model.containsAttribute("editForm")) {
            model.addAttribute("editForm", new PromotionFormDTO());
        }

        return "marketing";
    }

    /**
     * Thêm chương trình khuyến mãi
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

        // Kiểm tra logic ngày tháng hợp lệ
        if (form.getNgayBatDau() != null && form.getNgayKetThuc() != null
                && form.getNgayBatDau().isAfter(form.getNgayKetThuc())) {
            bindingResult.rejectValue("ngayKetThuc", "error.ngayKetThuc", "Ngày kết thúc phải bằng hoặc sau ngày bắt đầu");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addForm", bindingResult);
            redirectAttributes.addFlashAttribute("addForm", form);
            redirectAttributes.addFlashAttribute("hasAddError", true);
            return "redirect:/marketing";
        }

        try {
            marketingService.createPromotion(form);
            redirectAttributes.addFlashAttribute("successMsg", "Tạo chương trình khuyến mãi thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/marketing";
    }

    /**
     * Sửa chương trình khuyến mãi
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

        // Kiểm tra logic ngày tháng hợp lệ
        if (form.getNgayBatDau() != null && form.getNgayKetThuc() != null
                && form.getNgayBatDau().isAfter(form.getNgayKetThuc())) {
            bindingResult.rejectValue("ngayKetThuc", "error.ngayKetThuc", "Ngày kết thúc phải bằng hoặc sau ngày bắt đầu");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editForm", bindingResult);
            redirectAttributes.addFlashAttribute("editForm", form);
            redirectAttributes.addFlashAttribute("hasEditError", true);
            return "redirect:/marketing";
        }

        try {
            marketingService.updatePromotion(form);
            redirectAttributes.addFlashAttribute("successMsg", "Cập nhật khuyến mãi thành công!");
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
    public String deletePromotion(@RequestParam Integer maKhuyenMai, RedirectAttributes redirectAttributes) {
        try {
            marketingService.deletePromotion(maKhuyenMai);
            redirectAttributes.addFlashAttribute("successMsg", "Đã xóa chương trình khuyến mãi!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/marketing";
    }
}