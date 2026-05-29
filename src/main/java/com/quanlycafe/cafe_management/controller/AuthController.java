package com.quanlycafe.cafe_management.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * AuthController
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai        Create
 */
@Controller
public class AuthController {

    /**
     * Hiển thị trang login
     *
     * @return String
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
}