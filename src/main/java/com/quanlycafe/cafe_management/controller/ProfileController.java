package com.quanlycafe.cafe_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * ProfileController
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 */
@Controller
public class ProfileController {

    /**
     * Hiển thị trang profile
     *
     * @param model Model
     * @return String
     */
    @GetMapping("/profile")
    public String showProfile(Model model) {
        return "profile";
    }

    /**
     * Cập nhật cấu hình cửa hàng
     *
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/profile/update-business")
    public String updateBusiness(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("successMsg", "Đã cập nhật thành công cấu hình cửa hàng!");
        return "redirect:/profile";
    }

    /**
     * Thay đổi mật khẩu
     *
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/profile/change-password")
    public String changePassword(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("successMsg", "Đổi mật khẩu thành công!");
        return "redirect:/profile";
    }
}