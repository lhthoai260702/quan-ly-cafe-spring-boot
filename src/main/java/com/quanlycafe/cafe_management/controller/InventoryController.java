package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.InventoryFormDTO;
import com.quanlycafe.cafe_management.dto.StockActionDTO;
import com.quanlycafe.cafe_management.entity.HangHoa;
import com.quanlycafe.cafe_management.service.InventoryService;
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
 * InventoryController
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
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Hiển thị trang quản lý hàng hóa
     *
     * @param keyword Từ khóa tìm kiếm
     * @param page    int
     * @param size    int
     * @param model   Model
     * @return String
     */
    @GetMapping("/inventory")
    public String showInventory(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size,
            Model model) {

        // Tạo cấu hình phân trang, sắp xếp theo mã hàng hóa tăng dần
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.ASC, "maHangHoa"));
        Page<HangHoa> itemPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            itemPage = inventoryService.searchItems(keyword.trim(), pageable);
        } else {
            itemPage = inventoryService.getAllItems(pageable);
        }

        model.addAttribute("items", itemPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", itemPage.getTotalPages());

        model.addAttribute("units", inventoryService.getAllUnits());
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeTab", "inventory");

        // Khởi tạo các DTO rỗng để bind vào View
        if (!model.containsAttribute("addForm")) {
            model.addAttribute("addForm", new InventoryFormDTO());
        }
        if (!model.containsAttribute("editForm")) {
            model.addAttribute("editForm", new InventoryFormDTO());
        }
        if (!model.containsAttribute("importForm")) {
            model.addAttribute("importForm", new StockActionDTO());
        }
        if (!model.containsAttribute("exportForm")) {
            model.addAttribute("exportForm", new StockActionDTO());
        }

        return "inventory";
    }

    /**
     * Thêm hàng hóa
     */
    @PostMapping("/inventory/add")
    public String addItem(@Valid @ModelAttribute("addForm") InventoryFormDTO form,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addForm", bindingResult);
            redirectAttributes.addFlashAttribute("addForm", form);
            redirectAttributes.addFlashAttribute("hasAddError", true);
            return "redirect:/inventory";
        }
        inventoryService.createItem(form);
        redirectAttributes.addFlashAttribute("successMsg", "Thêm hàng hóa mới thành công!");
        return "redirect:/inventory";
    }

    /**
     * Sửa hàng hóa
     */
    @PostMapping("/inventory/edit")
    public String editItem(@Valid @ModelAttribute("editForm") InventoryFormDTO form,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editForm", bindingResult);
            redirectAttributes.addFlashAttribute("editForm", form);
            redirectAttributes.addFlashAttribute("hasEditError", true);
            return "redirect:/inventory";
        }
        inventoryService.updateItem(form);
        redirectAttributes.addFlashAttribute("successMsg", "Cập nhật thông tin thành công!");
        return "redirect:/inventory";
    }

    /**
     * Xóa hàng hóa
     */
    @PostMapping("/inventory/delete")
    public String deleteItem(@RequestParam Integer maHangHoa, RedirectAttributes redirectAttributes) {
        inventoryService.deleteItem(maHangHoa);
        redirectAttributes.addFlashAttribute("successMsg", "Đã xóa hàng hóa khỏi kho!");
        return "redirect:/inventory";
    }

    /**
     * Nhập hàng
     */
    @PostMapping("/inventory/import")
    public String importStock(@Valid @ModelAttribute("importForm") StockActionDTO form,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.importForm", bindingResult);
            redirectAttributes.addFlashAttribute("importForm", form);
            redirectAttributes.addFlashAttribute("hasImportError", true);
            return "redirect:/inventory";
        }
        inventoryService.importStock(form);
        redirectAttributes.addFlashAttribute("successMsg", "Đã cập nhật số lượng nhập kho!");
        return "redirect:/inventory";
    }

    /**
     * Xuất hàng
     */
    @PostMapping("/inventory/export")
    public String exportStock(@Valid @ModelAttribute("exportForm") StockActionDTO form,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.exportForm", bindingResult);
            redirectAttributes.addFlashAttribute("exportForm", form);
            redirectAttributes.addFlashAttribute("hasExportError", true);
            return "redirect:/inventory";
        }

        try {
            inventoryService.exportStock(form);
            redirectAttributes.addFlashAttribute("successMsg", "Đã cập nhật số lượng xuất kho!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/inventory";
    }
}