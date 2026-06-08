package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.MenuFormDTO;
import com.quanlycafe.cafe_management.entity.ChiTietThucDon;
import com.quanlycafe.cafe_management.entity.ThucDon;
import com.quanlycafe.cafe_management.repository.ChiTietThucDonRepository;
import com.quanlycafe.cafe_management.repository.HangHoaRepository;
import com.quanlycafe.cafe_management.service.MenuService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * MenuController
 * <p>
 * Version 1.4
 * <p>
 * Date: 30-05-2026
 * <p>
 * Copyright
 * <p>
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai      Create
 * 30-05-2026 lhthoai      Add Pagination, Sort by name ignore case, Java Convention
 * 07-06-2026 lhthoai      Integrate HangHoa for recipe/ingredients selection
 */
@Controller
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;
    private final HangHoaRepository hangHoaRepository;
    private final ChiTietThucDonRepository chiTietThucDonRepository;

    /**
     * Hiển thị trang quản lý thực đơn (có phân trang, sắp xếp theo tên, hiển thị tổng số lượng)
     *
     * @param category String
     * @param keyword  String
     * @param page     int
     * @param size     int
     * @param model    Model
     * @return String
     */
    @GetMapping("/menu")
    public String showMenuManager(
            @RequestParam(required = false, defaultValue = "all") String category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.asc("tenMon").ignoreCase()));
        Page<ThucDon> menuPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            menuPage = menuService.searchMenuItems(keyword.trim(), pageable);
        } else {
            menuPage = menuService.getMenuItemsByCategory(category, pageable);
        }

        List<String> categories = menuService.getAllCategories();

        model.addAttribute("menuItems", menuPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", menuPage.getTotalPages());
        model.addAttribute("totalItems", menuPage.getTotalElements());
        model.addAttribute("categories", categories);
        model.addAttribute("currentCategory", category);
        model.addAttribute("keyword", keyword);
        model.addAttribute("activeTab", "menu");

        // Truyền danh sách Hàng Hóa vào Model để hiển thị dropdown nguyên liệu
        model.addAttribute("listHangHoa", hangHoaRepository.findAll());

        if (!model.containsAttribute("addForm")) {
            model.addAttribute("addForm", new MenuFormDTO());
        }
        if (!model.containsAttribute("editForm")) {
            model.addAttribute("editForm", new MenuFormDTO());
        }

        return "menu";
    }

    /**
     * Lấy danh sách nguyên liệu
     *
     * @param maThucDon
     * @return
     */
    @GetMapping("/api/menu/ingredients/{maThucDon}")
    @ResponseBody
    public org.springframework.http.ResponseEntity<java.util.List<java.util.Map<String, Object>>> getIngredientsByThucDon(@PathVariable Integer maThucDon) {
        List<ChiTietThucDon> list = chiTietThucDonRepository.findByMaThucDon(maThucDon);
        java.util.List<java.util.Map<String, Object>> result = new java.util.ArrayList<>();

        for (ChiTietThucDon c : list) {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("maHangHoa", c.getMaHangHoa());
            map.put("khoiLuong", c.getKhoiLuong());
            map.put("donViTinh", c.getDonViTinh());

            // Lấy tên nguyên liệu để FE hiển thị
            hangHoaRepository.findById(c.getMaHangHoa()).ifPresent(hh -> {
                map.put("tenHangHoa", hh.getTenHangHoa());
            });
            result.add(map);
        }
        return org.springframework.http.ResponseEntity.ok(result);
    }

    /**
     * Thêm món mới vào thực đơn kèm thành phần
     *
     * @param form               MenuFormDTO
     * @param bindingResult      BindingResult
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/menu/add")
    public String addMenuItem(@Valid @ModelAttribute("addForm") MenuFormDTO form,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest request) {

        String referer = request.getHeader("Referer");
        String redirectUrl = referer != null ? referer : "/menu";

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addForm", bindingResult);
            redirectAttributes.addFlashAttribute("addForm", form);
            redirectAttributes.addFlashAttribute("hasAddError", true);
            return "redirect:" + redirectUrl;
        }

        try {
            menuService.createMenuItem(form);
            redirectAttributes.addFlashAttribute("successMsg", "Thêm món mới thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        return "redirect:" + redirectUrl;
    }

    /**
     * Cập nhật thông tin món trong thực đơn
     *
     * @param form               MenuFormDTO
     * @param bindingResult      BindingResult
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/menu/edit")
    public String editMenuItem(@Valid @ModelAttribute("editForm") MenuFormDTO form,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request) {

        String referer = request.getHeader("Referer");
        String redirectUrl = referer != null ? referer : "/menu";

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editForm", bindingResult);
            redirectAttributes.addFlashAttribute("editForm", form);
            redirectAttributes.addFlashAttribute("hasEditError", true);
            return "redirect:" + redirectUrl;
        }

        try {
            menuService.updateMenuItem(form);
            redirectAttributes.addFlashAttribute("successMsg", "Cập nhật món thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        return "redirect:" + redirectUrl;
    }

    /**
     * Xóa món khỏi thực đơn
     *
     * @param maThucDon          Integer
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/menu/delete")
    public String deleteMenuItem(@RequestParam Integer maThucDon,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request) {

        String referer = request.getHeader("Referer");
        String redirectUrl = referer != null ? referer : "/menu";

        try {
            menuService.deleteMenuItem(maThucDon);
            redirectAttributes.addFlashAttribute("successMsg", "Đã xóa món khỏi thực đơn!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        return "redirect:" + redirectUrl;
    }
}