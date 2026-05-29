package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.entity.KhuyenMai;
import com.quanlycafe.cafe_management.service.MarketingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MarketingController {

    private final MarketingService marketingService;

    // Hiển thị trang marketing
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

        return "marketing";
    }

    // Thêm chương trình khuyến mãi
    @PostMapping("/marketing/add")
    public String addPromotion(
            @RequestParam String tenKhuyenMai,
            @RequestParam String ngayBatDau,
            @RequestParam String ngayKetThuc,
            @RequestParam String loaiKhuyenMai,
            @RequestParam Double giaTriGiam,
            @RequestParam(required = false) String moTa) {
        try {
            LocalDate start = LocalDate.parse(ngayBatDau);
            LocalDate end = LocalDate.parse(ngayKetThuc);
            marketingService.createPromotion(tenKhuyenMai, start, end, loaiKhuyenMai, giaTriGiam, moTa);
            return "redirect:/marketing?success=add";
        } catch (Exception e) {
            return "redirect:/marketing?error=add";
        }
    }

    // Sửa chương trình khuyến mãi
    @PostMapping("/marketing/edit")
    public String editPromotion(
            @RequestParam Integer maKhuyenMai,
            @RequestParam String tenKhuyenMai,
            @RequestParam String ngayBatDau,
            @RequestParam String ngayKetThuc,
            @RequestParam String loaiKhuyenMai,
            @RequestParam Double giaTriGiam,
            @RequestParam(required = false) String moTa) {
        try {
            LocalDate start = LocalDate.parse(ngayBatDau);
            LocalDate end = LocalDate.parse(ngayKetThuc);
            marketingService.updatePromotion(maKhuyenMai, tenKhuyenMai, start, end, loaiKhuyenMai, giaTriGiam, moTa);
            return "redirect:/marketing?success=edit";
        } catch (Exception e) {
            return "redirect:/marketing?error=edit";
        }
    }

    // Xóa chương trình khuyến mãi
    @PostMapping("/marketing/delete")
    public String deletePromotion(@RequestParam Integer maKhuyenMai) {
        try {
            marketingService.deletePromotion(maKhuyenMai);
            return "redirect:/marketing?success=delete";
        } catch (Exception e) {
            return "redirect:/marketing?error=delete";
        }
    }
}