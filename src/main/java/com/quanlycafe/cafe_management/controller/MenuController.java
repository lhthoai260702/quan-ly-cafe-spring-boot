package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.entity.ThucDon;
import com.quanlycafe.cafe_management.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * MenuController
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
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
        model.addAttribute("activeTab", "menu"); // Để active menu sidebar nếu có cấu hình

        return "menu";
    }

    /**
     * Thêm món
     *
     * @param tenMon         String
     * @param giaTienHienTai Double
     * @param loaiMon        String
     * @return String
     */
    @PostMapping("/menu/add")
    public String addMenuItem(
            @RequestParam String tenMon,
            @RequestParam Double giaTienHienTai,
            @RequestParam String loaiMon
    ) {
        try {
            menuService.createMenuItem(tenMon, giaTienHienTai, loaiMon);
            return "redirect:/menu?success=add";
        } catch (Exception e) {
            return "redirect:/menu?error=add";
        }
    }

    /**
     * Sửa món
     *
     * @param maThucDon      Integer
     * @param tenMon         String
     * @param giaTienHienTai Double
     * @param loaiMon        String
     * @return String
     */
    @PostMapping("/menu/edit")
    public String editMenuItem(
            @RequestParam Integer maThucDon,
            @RequestParam String tenMon,
            @RequestParam Double giaTienHienTai,
            @RequestParam String loaiMon
    ) {
        try {
            menuService.updateMenuItem(maThucDon, tenMon, giaTienHienTai, loaiMon);
            return "redirect:/menu?success=edit";
        } catch (Exception e) {
            return "redirect:/menu?error=edit";
        }
    }

    /**
     * Xóa món
     *
     * @param maThucDon Integer
     * @return String
     */
    @PostMapping("/menu/delete")
    public String deleteMenuItem(@RequestParam Integer maThucDon) {
        try {
            menuService.deleteMenuItem(maThucDon);
            return "redirect:/menu?success=delete";
        } catch (Exception e) {
            return "redirect:/menu?error=delete";
        }
    }
}