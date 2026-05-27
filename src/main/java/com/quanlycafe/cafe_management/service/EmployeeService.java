package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.dto.UserProfileDTO;
import com.quanlycafe.cafe_management.entity.NhanVien;
import com.quanlycafe.cafe_management.repository.NhanVienRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final NhanVienRepository nhanVienRepository;

    // Hiển thị tất cả nhân viên
    public List<UserProfileDTO> getAllEmployees() {
        List<NhanVien> danhSachNhanVien = nhanVienRepository.findAll();
        return danhSachNhanVien.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    // Tiếp nhận bộ lọc 3 phần từ Controller gửi xuống
    public List<UserProfileDTO> getEmployeesByRoleType(String roleType) {
        String keyword = "";

        switch (roleType) {
            case "quanly":
                keyword = "Quản Lý";
                break;
            case "phucvu":
                keyword = "Phục Vụ";
                break;
            case "phache":
                keyword = "Pha Chế";
                break;
            default:
                return getAllEmployees(); // Nếu tham số không khớp, trả về tất cả
        }

        return nhanVienRepository.findByChucVu_TenChucVuContainingIgnoreCase(keyword)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Lấy thông tin nhân viên
    private UserProfileDTO mapToDTO(NhanVien nv) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setMaNhanVien(nv.getMaNhanVien());
        dto.setHoTen(nv.getHoTen());
        dto.setSoDienThoai(nv.getSoDienThoai());
        dto.setDiaChi(nv.getDiaChi());

        if (nv.getChucVu() != null) {
            dto.setTenChucVu(nv.getChucVu().getTenChucVu());
            dto.setLuong(nv.getChucVu().getLuong());
        }

        if (nv.getTaiKhoan() != null) {
            dto.setTenDangNhap(nv.getTaiKhoan().getTenDangNhap());
            dto.setQuyenHan(nv.getTaiKhoan().getQuyenHan());
            dto.setAnh(nv.getTaiKhoan().getAnh());
        }

        return dto;
    }

    // Search thông tin
    public List<UserProfileDTO> searchEmployees(String keyword) {
        return nhanVienRepository.findByHoTenContainingIgnoreCaseOrTaiKhoan_TenDangNhapContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
}