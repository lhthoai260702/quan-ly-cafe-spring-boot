package com.quanlycafe.cafe_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String showProfile(Model model) {
        return "profile";
    }

    @PostMapping("/profile/update-business")
    public String updateBusiness(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("successMsg", "Đã cập nhật thành công cấu hình cửa hàng!");
        return "redirect:/profile";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("successMsg", "Đổi mật khẩu thành công!");
        return "redirect:/profile";
    }
}