package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.UserProfileDTO;
import com.quanlycafe.cafe_management.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @PostMapping("/employees/add")
    public String addEmployee(
            @RequestParam String hoTen,
            @RequestParam String soDienThoai,
            @RequestParam String diaChi,
            @RequestParam Integer maChucVu,
            @RequestParam String tenDangNhap,
            @RequestParam String matKhau
    ) {
        try {
            employeeService.createEmployee(hoTen, soDienThoai, diaChi, maChucVu, tenDangNhap, matKhau);
            return "redirect:/employees?success";
        } catch (Exception e) {
            return "redirect:/employees?error";
        }
    }

    @PostMapping("/employees/edit")
    public String editEmployee(
            @RequestParam Integer maNhanVien,
            @RequestParam String hoTen,
            @RequestParam String soDienThoai,
            @RequestParam String diaChi,
            @RequestParam Integer maChucVu
    ) {
        try {
            employeeService.updateEmployee(maNhanVien, hoTen, soDienThoai, diaChi, maChucVu);
            return "redirect:/employees?success=edit";
        } catch (Exception e) {
            return "redirect:/employees?error=edit";
        }
    }

    @PostMapping("/employees/delete")
    public String deleteEmployee(@RequestParam Integer maNhanVien) {
        try {
            employeeService.deleteEmployee(maNhanVien);
            return "redirect:/employees?success=delete";
        } catch (Exception e) {
            return "redirect:/employees?error=delete";
        }
    }
}