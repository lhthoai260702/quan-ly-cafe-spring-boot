package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.EmployeeFormDTO;
import com.quanlycafe.cafe_management.dto.UserProfileDTO;
import com.quanlycafe.cafe_management.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * EmployeeController
 * * Version 1.1
 * * Date: 07-06-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 06-06-2026 lhthoai      Create and update logic
 * 07-06-2026 Quản Lý      Update Dynamic Role Filter
 */
@Controller
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * Hiển thị trang quản lý nhân viên (có phân trang, sắp xếp theo tên, lọc động)
     *
     * @param roleId  Integer (Mã chức vụ cần lọc)
     * @param keyword String
     * @param page    int
     * @param size    int
     * @param model   Model
     * @return String
     */
    @GetMapping("/employees")
    public String showEmployeeManager(
            @RequestParam(required = false, name = "role") Integer roleId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Order.asc("hoTen").ignoreCase()));
        Page<UserProfileDTO> employeePage;

        // Ưu tiên tìm kiếm từ khóa trước, nếu không thì lọc theo chức vụ
        if (keyword != null && !keyword.trim().isEmpty()) {
            employeePage = employeeService.searchEmployees(keyword.trim(), pageable);
        } else if (roleId != null) {
            employeePage = employeeService.getEmployeesByRoleId(roleId, pageable);
        } else {
            employeePage = employeeService.getAllEmployees(pageable);
        }

        model.addAttribute("employees", employeePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeePage.getTotalPages());
        model.addAttribute("totalEmployees", employeePage.getTotalElements());
        model.addAttribute("activeTab", "employee");
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedRole", roleId); // Trả lại ID được chọn để view highlight

        // Load danh sách chức vụ động từ DB ra giao diện
        model.addAttribute("listRoles", employeeService.getAllChucVu());

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
     * @param request            HttpServletRequest
     * @return String
     */
    @PostMapping("/employees/add")
    public String addEmployee(@Valid @ModelAttribute("addForm") EmployeeFormDTO form,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest request) {

        String referer = request.getHeader("Referer");
        String redirectUrl = referer != null ? referer : "/employees";

        if (form.getTenDangNhap() == null || form.getTenDangNhap().trim().isEmpty()) {
            bindingResult.addError(new FieldError("addForm", "tenDangNhap", "Tên đăng nhập không được để trống"));
        }

        if (form.getMatKhau() == null || form.getMatKhau().length() < 6) {
            bindingResult.addError(new FieldError("addForm", "matKhau", "Mật khẩu phải từ 6 ký tự trở lên"));
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addForm", bindingResult);
            redirectAttributes.addFlashAttribute("addForm", form);
            redirectAttributes.addFlashAttribute("hasAddError", true);

            return "redirect:" + redirectUrl;
        }

        try {
            employeeService.createEmployee(form);
            redirectAttributes.addFlashAttribute("successMsg", "Tuyển nhân sự thành công!");
        } catch (IllegalArgumentException ex) {
            bindingResult.addError(new FieldError("addForm", "tenDangNhap", ex.getMessage()));

            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.addForm", bindingResult);
            redirectAttributes.addFlashAttribute("addForm", form);
            redirectAttributes.addFlashAttribute("hasAddError", true);

            return "redirect:" + redirectUrl;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi hệ thống: " + e.getMessage());
        }

        return "redirect:" + redirectUrl;
    }

    /**
     * Sửa nhân viên
     *
     * @param form               EmployeeFormDTO
     * @param bindingResult      BindingResult
     * @param redirectAttributes RedirectAttributes
     * @param request            HttpServletRequest
     * @return String
     */
    @PostMapping("/employees/edit")
    public String editEmployee(@Valid @ModelAttribute("editForm") EmployeeFormDTO form,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request) {

        String referer = request.getHeader("Referer");
        String redirectUrl = referer != null ? referer : "/employees";

        boolean hasRealErrors = false;
        if (bindingResult.hasErrors()) {
            for (FieldError error : bindingResult.getFieldErrors()) {
                if (!error.getField().equals("tenDangNhap") && !error.getField().equals("matKhau")) {
                    hasRealErrors = true;
                    break;
                }
            }
        }

        if (hasRealErrors) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.editForm", bindingResult);
            redirectAttributes.addFlashAttribute("editForm", form);
            redirectAttributes.addFlashAttribute("hasEditError", true);

            return "redirect:" + redirectUrl;
        }

        try {
            employeeService.updateEmployee(form);
            redirectAttributes.addFlashAttribute("successMsg", "Cập nhật hồ sơ thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        return "redirect:" + redirectUrl;
    }

    /**
     * Xoá nhân viên
     *
     * @param maNhanVien
     * @param redirectAttributes
     * @param request
     * @return
     */
    @PostMapping("/employees/delete")
    public String deleteEmployee(@RequestParam Integer maNhanVien,
                                 RedirectAttributes redirectAttributes,
                                 HttpServletRequest request) {

        try {
            boolean isSelfDeleted = employeeService.deleteEmployee(maNhanVien);

            if (isSelfDeleted) {
                // Hủy session hiện tại và đăng xuất
                request.getSession().invalidate();
                return "redirect:/login?logout";
            }

            redirectAttributes.addFlashAttribute("successMsg", "Đã xóa nhân viên thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        return "redirect:/employees";
    }
}