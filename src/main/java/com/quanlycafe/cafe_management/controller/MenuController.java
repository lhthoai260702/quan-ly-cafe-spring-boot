package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.MenuFormDTO;
import com.quanlycafe.cafe_management.entity.ThucDon;
import com.quanlycafe.cafe_management.service.MenuService;
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
 * MenuController
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
public class MenuController {

    private final MenuService menuService;

    /**
     * Hiển thị trang menu
     *
     * @param category String
     * @param keyword  String
     * @param model    Model
     * @return String
     */
    @GetMapping("/menu")
    public String showMenuManager(
            @RequestParam(required = false, defaultValue = "all") String category,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<ThucDon> menuItems;

        // Ưu tiên tìm kiếm bằng từ khóa nếu có
        if (keyword != null && !keyword.trim().isEmpty()) {
            menuItems = menuService.searchMenuItems(keyword.trim());
        } else {
            menuItems = menuService.getMenuItemsByCategory(category);
        }

        List<String> categories = menuService.getAllCategories();

        model.addAttribute("menuItems", menuItems);
        model.addAttribute("categories", categories);
        model.addAttribute("currentCategory", category);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeTab", "menu");

        // Khởi tạo form rỗng nếu chưa có (tránh lỗi khi load trang lần đầu)
        if (!model.containsAttribute("addForm")) {
            model.addAttribute("addForm", new MenuFormDTO());
        }
        if (!model.containsAttribute("editForm")) {
            model.addAttribute("editForm", new MenuFormDTO());
        }

        return "menu";
    }

    /**
     * Thêm món
     *
     * @param form               MenuFormDTO
     * @param bindingResult      BindingResult
     * @param redirectAttributes RedirectAttributes
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
     * Sửa món
     *
     * @param form               MenuFormDTO
     * @param bindingResult      BindingResult
     * @param redirectAttributes RedirectAttributes
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
     * Xóa món
     *
     * @param maThucDon          Integer
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/menu/delete")
    public String deleteMenuItem(@RequestParam Integer maThucDon, RedirectAttributes redirectAttributes) {
        try {
            menuService.deleteMenuItem(maThucDon);
            redirectAttributes.addFlashAttribute("successMsg", "Đã xóa món khỏi thực đơn!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/menu";
    }
}