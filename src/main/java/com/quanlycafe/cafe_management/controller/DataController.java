package com.quanlycafe.cafe_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DataController {

    // Hiển thị trang quản lý dữ liệu
    @GetMapping("/data")
    public String showDataManagement(Model model) {
        return "data";
    }
}