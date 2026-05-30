package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.UserProfileDTO;
import com.quanlycafe.cafe_management.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ProfileController
 * * Version 1.1
 * * Date: 29-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 30-05-2026 lthoai       Check validation
 */
@Controller
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    /**
     * Hiển thị trang profile
     *
     * @param model Model
     * @return String
     */
    @GetMapping("/profile")
    public String showProfile(Model model) {
        // Lấy thông tin user hiện tại truyền xuống giao diện nếu chưa có
        if (!model.containsAttribute("currentUser")) {
            model.addAttribute("currentUser", profileService.getCurrentUserProfile());
        }
        return "profile";
    }

    /**
     * Cập nhật thông tin profile
     *
     * @param userProfileDTO     UserProfileDTO
     * @param bindingResult      BindingResult
     * @param redirectAttributes RedirectAttributes
     * @param model              Model
     * @return String
     */
    @PostMapping("/profile/update")
    public String updateProfile(@Valid @ModelAttribute("currentUser") UserProfileDTO userProfileDTO,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes,
                                Model model) {

        // Nếu Backend phát hiện lỗi
        if (bindingResult.hasErrors()) {
            UserProfileDTO currentDbInfo = profileService.getCurrentUserProfile();
            userProfileDTO.setAnh(currentDbInfo.getAnh());
            userProfileDTO.setTenChucVu(currentDbInfo.getTenChucVu());
            userProfileDTO.setTenDangNhap(currentDbInfo.getTenDangNhap());
            userProfileDTO.setLuong(currentDbInfo.getLuong());

            return "profile";
        }

        try {
            // Cập nhật xuống DB
            profileService.updateProfile(userProfileDTO);
            redirectAttributes.addFlashAttribute("successMsg", "Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Có lỗi xảy ra: " + e.getMessage());
        }

        return "redirect:/profile";
    }
}