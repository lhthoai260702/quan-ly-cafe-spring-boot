package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.dto.UserProfileDTO;
import com.quanlycafe.cafe_management.entity.NhanVien;
import com.quanlycafe.cafe_management.entity.TaiKhoan;
import com.quanlycafe.cafe_management.repository.NhanVienRepository;
import com.quanlycafe.cafe_management.repository.TaiKhoanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * ProfileService
 * * Version 1.1
 * * Date: 29-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 * 30-05-2026 lhthoai      Add PasswordEncoder & format by convention
 */
@Service
public class ProfileService {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Lấy thông tin user đang đăng nhập
     *
     * @return UserProfileDTO
     */
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

    /**
     * Cập nhật thông tin profile và mật khẩu
     *
     * @param dto
     * @throws Exception
     */
    @Transactional
    public void updateProfile(UserProfileDTO dto) throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails) ? ((UserDetails) principal).getUsername() : principal.toString();

        // 1. Cập nhật thông tin nhân viên
        Optional<NhanVien> nhanVienOpt = nhanVienRepository.findByTaiKhoan_TenDangNhap(username);
        if (nhanVienOpt.isPresent()) {
            NhanVien nv = nhanVienOpt.get();
            nv.setHoTen(dto.getHoTen());
            nv.setSoDienThoai(dto.getSoDienThoai());
            nhanVienRepository.save(nv);
        } else {
            throw new Exception("Không tìm thấy thông tin nhân viên!");
        }

        // 2. Nếu có nhập mật khẩu mới thì tiến hành mã hóa và cập nhật bảng Tài khoản
        if (dto.getMatKhauMoi() != null && !dto.getMatKhauMoi().trim().isEmpty()) {
            Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findByTenDangNhap(username);
            if (taiKhoanOpt.isPresent()) {
                TaiKhoan tk = taiKhoanOpt.get();

                // Mã hóa mật khẩu bằng Bcrypt trước khi lưu xuống DB
                tk.setMatKhau(passwordEncoder.encode(dto.getMatKhauMoi()));

                taiKhoanRepository.save(tk);
            }
        }
    }
}