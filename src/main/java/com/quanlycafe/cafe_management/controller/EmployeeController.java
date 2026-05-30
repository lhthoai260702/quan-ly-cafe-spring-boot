package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.EmployeeFormDTO;
import com.quanlycafe.cafe_management.dto.UserProfileDTO;
import com.quanlycafe.cafe_management.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * EmployeeController
 * * Version 1.1
 * * Date: 29-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 30-05-2026 Quản Lý      Format convention, add DTO Validation (PRG Pattern)
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
            employees = employeeService.searchEmployees(keyword.trim());
        } else if (role != null && !role.trim().isEmpty()) {
            employees = employeeService.getEmployeesByRoleType(role);
        } else {
            employees = employeeService.getAllEmployees();
        }

        model.addAttribute("employees", employees);
        model.addAttribute("activeTab", "employee");
        model.addAttribute("keyword", keyword);

        // Khởi tạo DTO rỗng nếu model chưa có (tránh lỗi khi load trang lần đầu)
        if (!model.containsAttribute("addForm")) {
            model.addAttribute("addForm", new EmployeeFormDTO());
        }
        if (!model.containsAttribute("editForm")) {
            model.addAttribute("editForm", new EmployeeFormDTO());
        }

        return "employees";
    }

    /**
     * Thêm nhân viên
     *
     * @param form               EmployeeFormDTO
     * @param bindingResult      BindingResult
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/employees/add")
    public String addEmployee(@Valid @ModelAttribute("addForm") EmployeeFormDTO form,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {

        // Kiểm tra thủ công tên đăng nhập và mật khẩu (vì chỉ bắt buộc khi thêm)
        if (form.getTenDangNhap() == null || form.getTenDangNhap().trim().isEmpty()) {
            bindingResult.addError(new FieldError("addForm", "tenDangNhap", "Tên đăng nhập không được để trống"));
        }
        if (form.getMatKhau() == null || form.getMatKhau().length() < 6) {
            bindingResult.addError(new FieldError("addForm", "matKhau", "Mật khẩu phải từ 6 ký tự trở lên"));
        }

        if (bindingResult.hasErrors()) {
            // Giữ lại form có lỗi và bật cờ để HTML tự động mở lại Modal Thêm
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addForm", bindingResult);
            redirectAttributes.addFlashAttribute("addForm", form);
            redirectAttributes.addFlashAttribute("hasAddError", true);
            return "redirect:/employees";
        }

        try {
            employeeService.createEmployee(form);
            redirectAttributes.addFlashAttribute("successMsg", "Tuyển nhân sự thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/employees";
    }

    /**
     * Sửa nhân viên
     *
     * @param form               EmployeeFormDTO
     * @param bindingResult      BindingResult
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/employees/edit")
    public String editEmployee(@Valid @ModelAttribute("editForm") EmployeeFormDTO form,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            // Giữ lại form có lỗi và bật cờ để HTML tự động mở lại Modal Sửa
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editForm", bindingResult);
            redirectAttributes.addFlashAttribute("editForm", form);
            redirectAttributes.addFlashAttribute("hasEditError", true);
            return "redirect:/employees";
        }

        try {
            employeeService.updateEmployee(form);
            redirectAttributes.addFlashAttribute("successMsg", "Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/employees";
    }

    /**
     * Xóa nhân viên
     *
     * @param maNhanVien Integer
     * @return String
     */
    @PostMapping("/employees/delete")
    public String deleteEmployee(@RequestParam Integer maNhanVien, RedirectAttributes redirectAttributes) {
        try {
            employeeService.deleteEmployee(maNhanVien);
            redirectAttributes.addFlashAttribute("successMsg", "Đã xóa nhân viên thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }
        return "redirect:/employees";
    }
}