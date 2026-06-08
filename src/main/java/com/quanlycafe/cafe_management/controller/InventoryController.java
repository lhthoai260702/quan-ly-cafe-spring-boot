package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.*;
import com.quanlycafe.cafe_management.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * InventoryController
 * Version 1.5
 * Date: 08-06-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 * 08-06-2026 lhthoai      Add Unit Filter, Retain URL params on Redirects
 */
@Controller
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/inventory")
    public String showInventory(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "0") Integer unit,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.asc("tenHangHoa").ignoreCase()));

        // Đã đổi Page<HangHoa> thành Page<InventoryItemDTO>
        Page<InventoryItemDTO> itemPage = inventoryService.searchAndFilterItems(keyword.trim(), unit, pageable);

        model.addAttribute("items", itemPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", itemPage.getTotalPages());
        model.addAttribute("totalItems", itemPage.getTotalElements());
        model.addAttribute("units", inventoryService.getAllUnits());
        model.addAttribute("keyword", keyword);
        model.addAttribute("unit", unit);
        model.addAttribute("activeTab", "inventory");

        if (!model.containsAttribute("addForm")) {
            model.addAttribute("addForm", new InventoryFormDTO());
        }
        if (!model.containsAttribute("editForm")) {
            model.addAttribute("editForm", new InventoryFormDTO());
        }
        if (!model.containsAttribute("importForm")) {
            model.addAttribute("importForm", new ImportStockDTO());
        }
        if (!model.containsAttribute("editHistoryForm")) {
            model.addAttribute("editHistoryForm", new DonNhapEditDTO());
        }

        return "inventory";
    }

    @PostMapping("/inventory/add")
    public String addItem(@Valid @ModelAttribute("addForm") InventoryFormDTO form,
                          BindingResult bindingResult,
                          @RequestParam(defaultValue = "") String keyword,
                          @RequestParam(defaultValue = "0") Integer unit,
                          @RequestParam(defaultValue = "1") int page,
                          RedirectAttributes redirectAttributes) {

        // Giữ lại URL params
        redirectAttributes.addAttribute("keyword", keyword);
        redirectAttributes.addAttribute("unit", unit);
        redirectAttributes.addAttribute("page", page);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addForm", bindingResult);
            redirectAttributes.addFlashAttribute("addForm", form);
            redirectAttributes.addFlashAttribute("hasAddError", true);
            return "redirect:/inventory";
        }

        try {
            inventoryService.createItem(form);
            redirectAttributes.addFlashAttribute("successMsg", "Nhập hàng hóa mới thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi khi nhập hàng mới: " + e.getMessage());
        }
        return "redirect:/inventory";
    }

    @PostMapping("/inventory/edit")
    public String editItem(@Valid @ModelAttribute("editForm") InventoryFormDTO form,
                           BindingResult bindingResult,
                           @RequestParam(defaultValue = "") String keyword,
                           @RequestParam(defaultValue = "0") Integer unit,
                           @RequestParam(defaultValue = "1") int page,
                           RedirectAttributes redirectAttributes) {

        redirectAttributes.addAttribute("keyword", keyword);
        redirectAttributes.addAttribute("unit", unit);
        redirectAttributes.addAttribute("page", page);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editForm", bindingResult);
            redirectAttributes.addFlashAttribute("editForm", form);
            redirectAttributes.addFlashAttribute("hasEditError", true);
            return "redirect:/inventory";
        }

        try {
            inventoryService.updateItem(form);
            redirectAttributes.addFlashAttribute("successMsg", "Cập nhật thông tin thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi khi cập nhật: " + e.getMessage());
        }
        return "redirect:/inventory";
    }

    @PostMapping("/inventory/delete")
    public String deleteItem(@RequestParam Integer maHangHoa,
                             @RequestParam(defaultValue = "") String keyword,
                             @RequestParam(defaultValue = "0") Integer unit,
                             @RequestParam(defaultValue = "1") int page,
                             RedirectAttributes redirectAttributes) {

        redirectAttributes.addAttribute("keyword", keyword);
        redirectAttributes.addAttribute("unit", unit);
        redirectAttributes.addAttribute("page", page);

        try {
            inventoryService.deleteItem(maHangHoa);
            redirectAttributes.addFlashAttribute("successMsg", "Đã xóa hàng hóa khỏi kho!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi không thể xóa: " + e.getMessage());
        }
        return "redirect:/inventory";
    }

    @PostMapping("/inventory/import")
    public String importStock(@Valid @ModelAttribute("importForm") ImportStockDTO form,
                              BindingResult bindingResult,
                              @RequestParam(defaultValue = "") String keyword,
                              @RequestParam(defaultValue = "0") Integer unit,
                              @RequestParam(defaultValue = "1") int page,
                              RedirectAttributes redirectAttributes) {

        redirectAttributes.addAttribute("keyword", keyword);
        redirectAttributes.addAttribute("unit", unit);
        redirectAttributes.addAttribute("page", page);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.importForm", bindingResult);
            redirectAttributes.addFlashAttribute("importForm", form);
            redirectAttributes.addFlashAttribute("hasImportError", true);
            return "redirect:/inventory";
        }

        try {
            inventoryService.importStock(form);
            redirectAttributes.addFlashAttribute("successMsg", "Đã cập nhật số lượng nhập kho!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi khi nhập kho: " + e.getMessage());
        }
        return "redirect:/inventory";
    }

    @GetMapping("/inventory/api/history/{id}")
    @ResponseBody
    public ResponseEntity<List<DonNhapHistoryDTO>> getHistory(@PathVariable Integer id) {
        return ResponseEntity.ok(inventoryService.getImportHistory(id));
    }

    @PostMapping("/inventory/history/edit")
    public String editHistory(@Valid @ModelAttribute("editHistoryForm") DonNhapEditDTO form,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Dữ liệu sửa đơn nhập không hợp lệ.");
            return "redirect:/inventory";
        }
        try {
            inventoryService.updateDonNhapHistory(form);
            redirectAttributes.addFlashAttribute("successMsg", "Đã cập nhật phiếu nhập hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/inventory";
    }

    @PostMapping("/inventory/history/delete")
    public String deleteHistory(@RequestParam Integer maDonNhap, RedirectAttributes redirectAttributes) {
        try {
            inventoryService.deleteDonNhapHistory(maDonNhap);
            redirectAttributes.addFlashAttribute("successMsg", "Đã xóa phiếu nhập hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/inventory";
    }
}