package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.EquipmentFormDTO;
import com.quanlycafe.cafe_management.entity.ThietBi;
import com.quanlycafe.cafe_management.service.EquipmentService;
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
 * EquipmentController
 * Version 1.2
 * Date: 29-05-2026
 * Copyright
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 30-05-2026 Quản Lý      Add PRG Validation & format convention
 * 06-06-2026 Quản Lý      Add Pagination & Sorting list
 */
@Controller
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    /**
     * Hiển thị trang quản lý thiết bị
     *
     * @param keyword Từ khóa tìm kiếm
     * @param page    Trang hiện tại (mặc định 1)
     * @param model   Model
     * @return String
     */
    @GetMapping("/equipment")
    public String showEquipmentManager(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "1") int page,
            Model model) {

        // Giới hạn 10 record/trang, sắp xếp theo tên tăng dần (không phân biệt hoa/thường)
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(Sort.Order.asc("tenThietBi").ignoreCase()));
        Page<ThietBi> equipmentPage;

        if (!keyword.trim().isEmpty()) {
            equipmentPage = equipmentService.searchEquipment(keyword.trim(), pageable);
        } else {
            equipmentPage = equipmentService.getAllEquipments(pageable);
        }

        model.addAttribute("equipments", equipmentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", equipmentPage.getTotalPages());
        model.addAttribute("totalItems", equipmentPage.getTotalElements());
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeTab", "equipment");

        // Khởi tạo DTO rỗng nếu model chưa có (tránh lỗi khi load trang)
        if (!model.containsAttribute("addForm")) {
            model.addAttribute("addForm", new EquipmentFormDTO());
        }
        if (!model.containsAttribute("editForm")) {
            model.addAttribute("editForm", new EquipmentFormDTO());
        }

        return "equipment";
    }

    /**
     * Thêm thiết bị
     *
     * @param form               EquipmentFormDTO
     * @param bindingResult      BindingResult
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/equipment/add")
    public String addEquipment(@Valid @ModelAttribute("addForm") EquipmentFormDTO form,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addForm", bindingResult);
            redirectAttributes.addFlashAttribute("addForm", form);
            redirectAttributes.addFlashAttribute("hasAddError", true);
            return "redirect:/equipment";
        }

        try {
            equipmentService.createEquipment(form);
            redirectAttributes.addFlashAttribute("successMsg", "Thêm thiết bị mới thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/equipment";
    }

    /**
     * Sửa thiết bị
     *
     * @param form               EquipmentFormDTO
     * @param bindingResult      BindingResult
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/equipment/edit")
    public String editEquipment(@Valid @ModelAttribute("editForm") EquipmentFormDTO form,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editForm", bindingResult);
            redirectAttributes.addFlashAttribute("editForm", form);
            redirectAttributes.addFlashAttribute("hasEditError", true);
            return "redirect:/equipment";
        }

        try {
            equipmentService.updateEquipment(form);
            redirectAttributes.addFlashAttribute("successMsg", "Cập nhật tài sản thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/equipment";
    }

    /**
     * Xóa thiết bị
     *
     * @param maThietBi          Integer
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/equipment/delete")
    public String deleteEquipment(@RequestParam Integer maThietBi, RedirectAttributes redirectAttributes) {
        try {
            equipmentService.deleteEquipment(maThietBi);
            redirectAttributes.addFlashAttribute("successMsg", "Đã xóa thiết bị khỏi hệ thống!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/equipment";
    }
}