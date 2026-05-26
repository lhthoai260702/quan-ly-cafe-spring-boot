package com.quanlycafe.cafe_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    // Trả về trang đăng nhập
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Trỏ đúng đến tên file login.html
    }

    // Tạm thời nếu người dùng vào trang chủ "/", cứ điều hướng họ ra bắt đăng nhập trước
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}