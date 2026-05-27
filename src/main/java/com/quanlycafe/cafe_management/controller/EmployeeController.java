package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.UserProfileDTO;
import com.quanlycafe.cafe_management.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/employees")
    public String showEmployeeManager(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<UserProfileDTO> employees;

        // 1. Nếu người dùng có nhập từ khóa tìm kiếm
        if (keyword != null && !keyword.trim().isEmpty()) {
            employees = employeeService.searchEmployees(keyword.trim());
        }
        // 2. Nếu người dùng bấm vào các nút bộ lọc chức vụ
        else if (role != null && !role.trim().isEmpty()) {
            employees = employeeService.getEmployeesByRoleType(role);
        }
        // 3. Mặc định hiển thị tất cả
        else {
            employees = employeeService.getAllEmployees();
        }

        model.addAttribute("employees", employees);
        model.addAttribute("activeTab", "employee");

        // Giữ lại từ khóa để gán ngược lại ô input
        model.addAttribute("keyword", keyword);

        return "employees";
    }
}