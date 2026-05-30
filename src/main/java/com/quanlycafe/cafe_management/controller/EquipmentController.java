package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.EquipmentFormDTO;
import com.quanlycafe.cafe_management.entity.ThietBi;
import com.quanlycafe.cafe_management.service.EquipmentService;
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
 * EquipmentController
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
public class EquipmentController {

    private final EquipmentService equipmentService;

    /**
     * Hiển thị trang quản lý thiết bị
     *
     * @param keyword Từ khóa tìm kiếm
     * @param model   Model
     * @return String
     */
    @GetMapping("/equipment")
    public String showEquipmentManager(
            @RequestParam(required = false) String keyword,
            Model model) {

        List<ThietBi> equipments;
        if (keyword != null && !keyword.trim().isEmpty()) {
            equipments = equipmentService.searchEquipment(keyword.trim());
        } else {
            equipments = equipmentService.getAllEquipments();
        }

        model.addAttribute("equipments", equipments);
        model.addAttribute("activeTab", "equipment");
        model.addAttribute("keyword", keyword);

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