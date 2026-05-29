package com.quanlycafe.cafe_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * HomeController
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 */
@Controller
public class HomeController {

    /**
     * Hiển thị trang chủ
     *
     * @return String
     */
    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }
}