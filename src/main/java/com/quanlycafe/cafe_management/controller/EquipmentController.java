package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.entity.ThietBi;
import com.quanlycafe.cafe_management.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

/**
 * EquipmentController
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai        Create
 */
@Controller
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    /**
     * Hiển thị trang quản lý thiết bị
     *
     * @param keyword
     * @param model
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

        return "equipment";
    }

    /**
     * Thêm thiết bị
     *
     * @param tenThietBi
     * @param soLuong
     * @param ghiChu
     * @param ngayMua
     * @param donGiaMua
     * @return String
     */
    @PostMapping("/equipment/add")
    public String addEquipment(
            @RequestParam String tenThietBi,
            @RequestParam Integer soLuong,
            @RequestParam(required = false) String ghiChu,
            @RequestParam(required = false) String ngayMua,
            @RequestParam(required = false) Double donGiaMua
    ) {
        try {
            LocalDate parsedDate = (ngayMua != null && !ngayMua.trim().isEmpty()) ? LocalDate.parse(ngayMua) : null;
            equipmentService.createEquipment(tenThietBi, soLuong, ghiChu, parsedDate, donGiaMua);
            return "redirect:/equipment?success=add";
        } catch (Exception e) {
            return "redirect:/equipment?error=add";
        }
    }

    /**
     * Sửa thiết bị
     *
     * @param maThietBi
     * @param tenThietBi
     * @param soLuong
     * @param ghiChu
     * @param ngayMua
     * @param donGiaMua
     * @return String
     */
    @PostMapping("/equipment/edit")
    public String editEquipment(
            @RequestParam Integer maThietBi,
            @RequestParam String tenThietBi,
            @RequestParam Integer soLuong,
            @RequestParam(required = false) String ghiChu,
            @RequestParam(required = false) String ngayMua,
            @RequestParam(required = false) Double donGiaMua
    ) {
        try {
            LocalDate parsedDate = (ngayMua != null && !ngayMua.trim().isEmpty()) ? LocalDate.parse(ngayMua) : null;
            equipmentService.updateEquipment(maThietBi, tenThietBi, soLuong, ghiChu, parsedDate, donGiaMua);
            return "redirect:/equipment?success=edit";
        } catch (Exception e) {
            return "redirect:/equipment?error=edit";
        }
    }

    /**
     * Xóa thiết bị
     *
     * @param maThietBi
     * @return String
     */
    @PostMapping("/equipment/delete")
    public String deleteEquipment(@RequestParam Integer maThietBi) {
        try {
            equipmentService.deleteEquipment(maThietBi);
            return "redirect:/equipment?success=delete";
        } catch (Exception e) {
            return "redirect:/equipment?error=delete";
        }
    }
}