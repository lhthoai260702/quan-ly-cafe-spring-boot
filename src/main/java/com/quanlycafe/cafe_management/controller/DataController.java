package com.quanlycafe.cafe_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * DataController
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai        Create
 */
@Controller
public class DataController {

    /**
     * Hiển thị trang quản lý dữ liệu
     *
     * @param model
     * @return String
     */
    @GetMapping("/data")
    public String showDataManagement(Model model) {
        return "data";
    }
}