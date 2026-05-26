package com.quanlycafe.cafe_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    // Hiển thị trang cá nhân
    @GetMapping("/profile")
    public String showProfile(Model model) {
        // Sau này em gọi Service để lấy dữ liệu từ DB vào đây
        return "profile"; // Trả về file profile.html
    }

    // Xử lý lưu nghiệp vụ cửa hàng
    @PostMapping("/profile/update-business")
    public String updateBusiness(RedirectAttributes redirectAttributes) {
        // Xử lý logic lưu dữ liệu vào DB tại đây
        redirectAttributes.addFlashAttribute("successMsg", "Đã cập nhật thành công cấu hình cửa hàng!");
        return "redirect:/profile";
    }

    // Xử lý đổi mật khẩu
    @PostMapping("/profile/change-password")
    public String changePassword(RedirectAttributes redirectAttributes) {
        // Xử lý logic check mật khẩu cũ và đổi mật khẩu mới
        redirectAttributes.addFlashAttribute("successMsg", "Đổi mật khẩu thành công!");
        return "redirect:/profile";
    }
}