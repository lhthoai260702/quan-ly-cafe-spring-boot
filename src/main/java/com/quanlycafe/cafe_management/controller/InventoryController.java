package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.entity.HangHoa;
import com.quanlycafe.cafe_management.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/inventory")
    public String showInventory(
            @RequestParam(required = false) String keyword,
            Model model) {

        List<HangHoa> items;
        if (keyword != null && !keyword.trim().isEmpty()) {
            items = inventoryService.searchItems(keyword.trim());
        } else {
            items = inventoryService.getAllItems();
        }

        model.addAttribute("items", items);
        model.addAttribute("units", inventoryService.getAllUnits());
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeTab", "inventory"); // Highlight menu

        return "inventory";
    }

    @PostMapping("/inventory/add")
    public String addItem(@RequestParam String tenHangHoa, @RequestParam Double soLuong,
                          @RequestParam Integer maDonViTinh, @RequestParam Double donGia) {
        inventoryService.createItem(tenHangHoa, soLuong, maDonViTinh, donGia);
        return "redirect:/inventory?success=add";
    }

    @PostMapping("/inventory/edit")
    public String editItem(@RequestParam Integer maHangHoa, @RequestParam String tenHangHoa,
                           @RequestParam Integer maDonViTinh, @RequestParam Double donGia) {
        inventoryService.updateItem(maHangHoa, tenHangHoa, maDonViTinh, donGia);
        return "redirect:/inventory?success=edit";
    }

    @PostMapping("/inventory/delete")
    public String deleteItem(@RequestParam Integer maHangHoa) {
        inventoryService.deleteItem(maHangHoa);
        return "redirect:/inventory?success=delete";
    }

    @PostMapping("/inventory/import")
    public String importStock(@RequestParam Integer maHangHoa, @RequestParam Double soLuongNhap) {
        inventoryService.importStock(maHangHoa, soLuongNhap);
        return "redirect:/inventory?success=import";
    }

    @PostMapping("/inventory/export")
    public String exportStock(@RequestParam Integer maHangHoa, @RequestParam Double soLuongXuat) {
        try {
            inventoryService.exportStock(maHangHoa, soLuongXuat);
            return "redirect:/inventory?success=export";
        } catch (Exception e) {
            return "redirect:/inventory?error=export_exceed";
        }
    }
}