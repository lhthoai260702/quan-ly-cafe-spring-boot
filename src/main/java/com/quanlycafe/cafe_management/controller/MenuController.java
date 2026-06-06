package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.MenuFormDTO;
import com.quanlycafe.cafe_management.entity.ThucDon;
import com.quanlycafe.cafe_management.service.MenuService;
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

import java.util.List;

/**
 * MenuController
 * <p>
 * Version 1.3
 * <p>
 * Date: 30-05-2026
 * <p>
 * Copyright
 * <p>
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai      Create
 * 30-05-2026 lhthoai      Add Pagination, Sort by name ignore case, Java Convention
 */
@Controller
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    /**
     * Hiển thị trang quản lý thực đơn (có phân trang, sắp xếp theo tên, hiển thị tổng số lượng)
     * * @param category
     *
     * @param keyword
     * @param page
     * @param size
     * @param model
     * @return String
     */
    @GetMapping("/menu")
    public String showMenuManager(
            @RequestParam(required = false, defaultValue = "all") String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "16") int size,
            Model model) {

        // Bổ sung sắp xếp theo tên món không phân biệt in hoa/thường
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.asc("tenMon").ignoreCase()));
        Page<ThucDon> menuPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            menuPage = menuService.searchMenuItems(keyword.trim(), pageable);
        } else {
            menuPage = menuService.getMenuItemsByCategory(category, pageable);
        }

        List<String> categories = menuService.getAllCategories();

        model.addAttribute("menuItems", menuPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", menuPage.getTotalPages());
        // Bổ sung tổng số lượng record
        model.addAttribute("totalItems", menuPage.getTotalElements());

        model.addAttribute("categories", categories);
        model.addAttribute("currentCategory", category);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeTab", "menu");

        if (!model.containsAttribute("addForm")) {
            model.addAttribute("addForm", new MenuFormDTO());
        }
        if (!model.containsAttribute("editForm")) {
            model.addAttribute("editForm", new MenuFormDTO());
        }

        return "menu";
    }

    /**
     * Thêm món mới vào thực đơn
     * * @param form
     *
     * @param bindingResult
     * @param redirectAttributes
     * @return String
     */
    @PostMapping("/menu/add")
    public String addMenuItem(@Valid @ModelAttribute("addForm") MenuFormDTO form,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addForm", bindingResult);
            redirectAttributes.addFlashAttribute("addForm", form);
            redirectAttributes.addFlashAttribute("hasAddError", true);
            return "redirect:/menu";
        }

        try {
            menuService.createMenuItem(form);
            redirectAttributes.addFlashAttribute("successMsg", "Thêm món mới thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        return "redirect:/menu";
    }

    /**
     * Cập nhật thông tin món trong thực đơn
     * * @param form
     *
     * @param bindingResult
     * @param redirectAttributes
     * @return String
     */
    @PostMapping("/menu/edit")
    public String editMenuItem(@Valid @ModelAttribute("editForm") MenuFormDTO form,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editForm", bindingResult);
            redirectAttributes.addFlashAttribute("editForm", form);
            redirectAttributes.addFlashAttribute("hasEditError", true);
            return "redirect:/menu";
        }

        try {
            menuService.updateMenuItem(form);
            redirectAttributes.addFlashAttribute("successMsg", "Cập nhật món thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        return "redirect:/menu";
    }

    /**
     * Xóa món khỏi thực đơn
     * * @param maThucDon
     *
     * @param redirectAttributes
     * @return String
     */
    @PostMapping("/menu/delete")
    public String deleteMenuItem(@RequestParam Integer maThucDon,
                                 RedirectAttributes redirectAttributes) {
        try {
            menuService.deleteMenuItem(maThucDon);
            redirectAttributes.addFlashAttribute("successMsg", "Đã xóa món khỏi thực đơn!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        return "redirect:/menu";
    }
}