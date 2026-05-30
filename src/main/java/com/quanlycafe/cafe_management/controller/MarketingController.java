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
 * * Version 1.2
 * * Date: 30-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 30-05-2026 lthoai      Add PRG Validation & format convention
 * 30-05-2026 lthoai      Add Pagination
 */
@Controller
@RequiredArgsConstructor
public class MarketingController {

    private final MarketingService marketingService;

    /**
     * Hiển thị trang marketing
     *
     * @param keyword Từ khóa tìm kiếm
     * @param page    int
     * @param size    int
     * @param model   Model
     * @return String
     */
    @GetMapping("/marketing")
    public String showMarketing(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size,
            Model model) {

        // Tạo cấu hình phân trang, sắp xếp theo ID khuyến mãi giảm dần (mới nhất lên đầu)
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "maKhuyenMai"));
        Page<KhuyenMai> promoPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            promoPage = marketingService.searchPromotions(keyword.trim(), pageable);
        } else {
            promoPage = marketingService.getAllPromotions(pageable);
        }

        model.addAttribute("promotions", promoPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", promoPage.getTotalPages());

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

        // 1. Kiểm tra logic ngày tháng hợp lệ
        if (form.getNgayBatDau() != null && form.getNgayKetThuc() != null
                && form.getNgayBatDau().isAfter(form.getNgayKetThuc())) {
            bindingResult.rejectValue("ngayKetThuc", "error.ngayKetThuc", "Ngày kết thúc phải bằng hoặc sau ngày bắt đầu");
        }

        // 2. Kiểm tra chống tràn dữ liệu DECIMAL(10,2) và logic Phần trăm
        if (form.getGiaTriGiam() != null) {
            if ("Phần trăm".equals(form.getLoaiKhuyenMai()) && form.getGiaTriGiam() > 100) {
                bindingResult.rejectValue("giaTriGiam", "error.giaTriGiam", "Mức giảm theo phần trăm không được vượt quá 100%");
            } else if ("Số tiền".equals(form.getLoaiKhuyenMai()) && form.getGiaTriGiam() > 99999999) {
                bindingResult.rejectValue("giaTriGiam", "error.giaTriGiam", "Mức giảm tiền mặt không được vượt quá 99.999.999 VNĐ");
            }
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
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi hệ thống: " + e.getMessage());
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

        // 1. Kiểm tra logic ngày tháng hợp lệ
        if (form.getNgayBatDau() != null && form.getNgayKetThuc() != null
                && form.getNgayBatDau().isAfter(form.getNgayKetThuc())) {
            bindingResult.rejectValue("ngayKetThuc", "error.ngayKetThuc", "Ngày kết thúc phải bằng hoặc sau ngày bắt đầu");
        }

        // 2. Kiểm tra chống tràn dữ liệu DECIMAL(10,2) và logic Phần trăm
        if (form.getGiaTriGiam() != null) {
            if ("Phần trăm".equals(form.getLoaiKhuyenMai()) && form.getGiaTriGiam() > 100) {
                bindingResult.rejectValue("giaTriGiam", "error.giaTriGiam", "Mức giảm theo phần trăm không được vượt quá 100%");
            } else if ("Số tiền".equals(form.getLoaiKhuyenMai()) && form.getGiaTriGiam() > 99999999) {
                bindingResult.rejectValue("giaTriGiam", "error.giaTriGiam", "Mức giảm tiền mặt không được vượt quá 99.999.999 VNĐ");
            }
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
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi hệ thống: " + e.getMessage());
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