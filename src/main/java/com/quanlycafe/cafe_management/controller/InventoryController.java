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

/**
 * InventoryController
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai        Create
 */
@Controller
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Hiển thị trang quản lý hàng hóa
     *
     * @param keyword
     * @param model
     * @return String
     */
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
        model.addAttribute("activeTab", "inventory");

        return "inventory";
    }

    /**
     * Thêm hàng hóa
     *
     * @param tenHangHoa
     * @param soLuong
     * @param maDonViTinh
     * @param donGia
     * @return String
     */
    @PostMapping("/inventory/add")
    public String addItem(@RequestParam String tenHangHoa, @RequestParam Double soLuong,
                          @RequestParam Integer maDonViTinh, @RequestParam Double donGia) {
        inventoryService.createItem(tenHangHoa, soLuong, maDonViTinh, donGia);
        return "redirect:/inventory?success=add";
    }

    /**
     * Sửa hàng hóa
     *
     * @param maHangHoa
     * @param tenHangHoa
     * @param maDonViTinh
     * @param donGia
     * @return String
     */
    @PostMapping("/inventory/edit")
    public String editItem(@RequestParam Integer maHangHoa, @RequestParam String tenHangHoa,
                           @RequestParam Integer maDonViTinh, @RequestParam Double donGia) {
        inventoryService.updateItem(maHangHoa, tenHangHoa, maDonViTinh, donGia);
        return "redirect:/inventory?success=edit";
    }

    /**
     * Xóa hàng hóa
     *
     * @param maHangHoa
     * @return String
     */
    @PostMapping("/inventory/delete")
    public String deleteItem(@RequestParam Integer maHangHoa) {
        inventoryService.deleteItem(maHangHoa);
        return "redirect:/inventory?success=delete";
    }

    /**
     * Nhập hàng
     *
     * @param maHangHoa
     * @param soLuongNhap
     * @return String
     */
    @PostMapping("/inventory/import")
    public String importStock(@RequestParam Integer maHangHoa, @RequestParam Double soLuongNhap) {
        inventoryService.importStock(maHangHoa, soLuongNhap);
        return "redirect:/inventory?success=import";
    }

    /**
     * Xuất hàng
     *
     * @param maHangHoa
     * @param soLuongXuat
     * @return String
     */
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