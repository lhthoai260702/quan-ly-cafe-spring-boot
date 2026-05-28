package com.quanlycafe.cafe_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {
    // Hiển thị trang dashboard
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}