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

@Controller
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

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