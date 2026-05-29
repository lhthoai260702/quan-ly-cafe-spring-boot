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

/**
 * EmployeeController
 * * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 */
@Controller
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * Hiển thị trang quản lý nhân viên
     *
     * @param role    String
     * @param keyword String
     * @param model   Model
     * @return String
     */
    @GetMapping("/employees")
    public String showEmployeeManager(
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String keyword,
            Model model) {

        List<UserProfileDTO> employees;

        if (keyword != null && !keyword.trim().isEmpty()) {
            // 1. Nếu người dùng có nhập từ khóa tìm kiếm
            employees = employeeService.searchEmployees(keyword.trim());
        } else if (role != null && !role.trim().isEmpty()) {
            // 2. Nếu người dùng bấm vào các nút bộ lọc chức vụ
            employees = employeeService.getEmployeesByRoleType(role);
        } else {
            // 3. Mặc định hiển thị tất cả
            employees = employeeService.getAllEmployees();
        }

        model.addAttribute("employees", employees);
        model.addAttribute("activeTab", "employee");

        // Giữ lại từ khóa để gán ngược lại ô input
        model.addAttribute("keyword", keyword);

        return "employees";
    }

    /**
     * Thêm nhân viên
     *
     * @param hoTen       String
     * @param soDienThoai String
     * @param diaChi      String
     * @param maChucVu    Integer
     * @param tenDangNhap String
     * @param matKhau     String
     * @return String
     */
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

    /**
     * Sửa nhân viên
     *
     * @param maNhanVien  Integer
     * @param hoTen       String
     * @param soDienThoai String
     * @param diaChi      String
     * @param maChucVu    Integer
     * @return String
     */
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

    /**
     * Xóa nhân viên
     *
     * @param maNhanVien Integer
     * @return String
     */
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