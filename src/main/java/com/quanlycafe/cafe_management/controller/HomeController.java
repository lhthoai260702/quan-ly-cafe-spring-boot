package com.quanlycafe.cafe_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // Hiển thị trang chủ
    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }
}