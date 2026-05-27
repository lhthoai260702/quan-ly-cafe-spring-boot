package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.dto.UserProfileDTO;
import com.quanlycafe.cafe_management.entity.NhanVien;
import com.quanlycafe.cafe_management.entity.TaiKhoan;
import com.quanlycafe.cafe_management.repository.NhanVienRepository;
import com.quanlycafe.cafe_management.repository.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    // Hàm lấy thông tin user đang đăng nhập
    public UserProfileDTO getCurrentUserProfile() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        UserProfileDTO dto = new UserProfileDTO();

        // Lấy thông tin Tài khoản
        Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findByTenDangNhap(username);
        if (taiKhoanOpt.isPresent()) {
            TaiKhoan tk = taiKhoanOpt.get();
            dto.setTenDangNhap(tk.getTenDangNhap());
            dto.setQuyenHan(tk.getQuyenHan());
            dto.setAnh(tk.getAnh() != null ? tk.getAnh() : "default-avatar.png");
        }

        // Lấy thông tin Nhân viên và Chức vụ
        Optional<NhanVien> nhanVienOpt = nhanVienRepository.findByTaiKhoan_TenDangNhap(username);
        if (nhanVienOpt.isPresent()) {
            NhanVien nv = nhanVienOpt.get();
            dto.setMaNhanVien(nv.getMaNhanVien());
            dto.setHoTen(nv.getHoTen());
            dto.setSoDienThoai(nv.getSoDienThoai());

            if (nv.getChucVu() != null) {
                dto.setTenChucVu(nv.getChucVu().getTenChucVu());
                dto.setLuong(nv.getChucVu().getLuong());
            }
        } else {
            dto.setHoTen("Admin (Chưa thiết lập NV)");
        }

        return dto;
    }
}