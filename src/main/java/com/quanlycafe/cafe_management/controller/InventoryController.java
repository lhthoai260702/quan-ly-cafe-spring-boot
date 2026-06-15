package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.DonNhapHistoryDTO;
import com.quanlycafe.cafe_management.dto.InventoryFormDTO;
import com.quanlycafe.cafe_management.dto.InventoryItemDTO;
import com.quanlycafe.cafe_management.service.InventoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * InventoryController
 * Version 1.6
 * Date: 12-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 09-06-2026   lhthoai     Apply Java Coding Convention
 * 12-06-2026   Quản Lý     Tối ưu hóa: Dùng DTO đa năng, bỏ @Valid để form động
 */
@Controller
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /**
     * Hiển thị trang inventory
     *
     * @param keyword
     * @param unit
     * @param page
     * @param size
     * @param model
     * @return
     */
    @GetMapping("/inventory")
    public String showInventory(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(required = false, defaultValue = "0") Integer unit,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.asc("tenHangHoa").ignoreCase()));
        Page<InventoryItemDTO> itemPage = inventoryService.searchAndFilterItems(keyword.trim(), unit, pageable);

        model.addAttribute("items", itemPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", itemPage.getTotalPages());
        model.addAttribute("totalItems", itemPage.getTotalElements());
        model.addAttribute("units", inventoryService.getAllUnits());
        model.addAttribute("keyword", keyword);
        model.addAttribute("unit", unit);
        model.addAttribute("activeTab", "inventory");

        if (!model.containsAttribute("form")) {
            model.addAttribute("form", new InventoryFormDTO()); // Chỉ dùng 1 Object duy nhất
        }

        return "inventory";
    }

    /**
     * Lấy đơn nhập
     *
     * @param id
     * @return
     */
    @GetMapping("/inventory/api/history/{id}")
    @ResponseBody
    public ResponseEntity<List<DonNhapHistoryDTO>> getHistory(@PathVariable Integer id) {
        return ResponseEntity.ok(inventoryService.getImportHistory(id));
    }

    /**
     * Thêm hàng hoá
     *
     * @param form
     * @param redirectAttributes
     * @param request
     * @return
     */
    @PostMapping("/inventory/add")
    public String addItem(@ModelAttribute("form") InventoryFormDTO form,
                          RedirectAttributes redirectAttributes,
                          HttpServletRequest request) {

        String referer = request.getHeader("Referer");
        String redirectUrl = referer != null ? referer : "/inventory";

        try {
            inventoryService.createItem(form);
            redirectAttributes.addFlashAttribute("successMsg", "Nhập hàng hóa mới thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + redirectUrl;
    }

    /**
     * Chỉnh sửa hàng hoá
     *
     * @param form
     * @param redirectAttributes
     * @param request
     * @return
     */
    @PostMapping("/inventory/edit")
    public String editItem(@ModelAttribute("form") InventoryFormDTO form,
                           RedirectAttributes redirectAttributes,
                           HttpServletRequest request) {


        String referer = request.getHeader("Referer");
        String redirectUrl = referer != null ? referer : "/inventory";

        try {
            inventoryService.updateItem(form);
            redirectAttributes.addFlashAttribute("successMsg", "Cập nhật thông tin thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + redirectUrl;
    }

    /**
     * Thêm đơn nhập
     *
     * @param form
     * @param redirectAttributes
     * @param request
     * @return
     */
    @PostMapping("/inventory/import")
    public String importStock(@ModelAttribute("form") InventoryFormDTO form,
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest request) {


        String referer = request.getHeader("Referer");
        String redirectUrl = referer != null ? referer : "/inventory";

        try {
            inventoryService.importStock(form);
            redirectAttributes.addFlashAttribute("successMsg", "Đã cập nhật số lượng nhập kho!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + redirectUrl;
    }

    /**
     * Chỉnh sửa đơn nhập
     *
     * @param form
     * @param redirectAttributes
     * @param request
     * @return
     */
    @PostMapping("/inventory/history/edit")
    public String editHistory(@ModelAttribute("form") InventoryFormDTO form,
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest request) {


        String referer = request.getHeader("Referer");
        String redirectUrl = referer != null ? referer : "/inventory";

        try {
            inventoryService.updateDonNhapHistory(form);
            redirectAttributes.addFlashAttribute("successMsg", "Đã cập nhật phiếu nhập hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + redirectUrl;
    }

    /**
     * Xoá hàng hoá
     *
     * @param maHangHoa
     * @param redirectAttributes
     * @param request
     * @return
     */
    @PostMapping("/inventory/delete")
    public String deleteItem(@RequestParam Integer maHangHoa,
                             RedirectAttributes redirectAttributes,
                             HttpServletRequest request) {


        String referer = request.getHeader("Referer");
        String redirectUrl = referer != null ? referer : "/inventory";

        try {
            inventoryService.deleteItem(maHangHoa);
            redirectAttributes.addFlashAttribute("successMsg", "Đã xóa hàng hóa khỏi kho!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + redirectUrl;
    }

    /**
     * Xoá phiếu nhập hàng
     *
     * @param maDonNhap
     * @param redirectAttributes
     * @param request
     * @return
     */
    @PostMapping("/inventory/history/delete")
    public String deleteHistory(@RequestParam Integer maDonNhap,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request) {

        String referer = request.getHeader("Referer");
        String redirectUrl = referer != null ? referer : "/inventory";

        try {
            inventoryService.deleteDonNhapHistory(maDonNhap);
            redirectAttributes.addFlashAttribute("successMsg", "Đã xóa phiếu nhập hàng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:" + redirectUrl;
    }
}