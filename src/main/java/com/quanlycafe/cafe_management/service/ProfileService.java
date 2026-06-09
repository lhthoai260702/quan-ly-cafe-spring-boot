package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.dto.UserProfileDTO;
import com.quanlycafe.cafe_management.entity.NhanVien;
import com.quanlycafe.cafe_management.entity.TaiKhoan;
import com.quanlycafe.cafe_management.repository.NhanVienRepository;
import com.quanlycafe.cafe_management.repository.TaiKhoanRepository;
import com.quanlycafe.cafe_management.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;

/**
 * ProfileService
 * Version 1.4
 * Date: 09-06-2026
 * Copyright
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 * 30-05-2026 lhthoai       Add PasswordEncoder & format by convention
 * 08-06-2026 lhthoai       Add Avatar upload logic in updateProfile
 * 09-06-2026 Quản Lý       Fix validation matKhauMoi & Thêm logic reload Security Context cho Header
 */
@Service
public class ProfileService {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private TaiKhoanRepository taiKhoanRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService customUserDetailsService; // Tiêm dịch vụ UserDetails vào để nạp lại dữ liệu

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
            dto.setAnh(tk.getAnh() != null ? tk.getAnh() : "user.png");
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
     * Cập nhật thông tin profile, mật khẩu và ảnh đại diện
     *
     * @param dto UserProfileDTO
     * @throws Exception Error
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

        // 2. Cập nhật Mật khẩu và Ảnh đại diện trong bảng TaiKhoan
        Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findByTenDangNhap(username);
        if (taiKhoanOpt.isPresent()) {
            TaiKhoan tk = taiKhoanOpt.get();
            boolean isTkChanged = false;

            // Xử lý đổi mật khẩu
            if (dto.getMatKhauMoi() != null && !dto.getMatKhauMoi().trim().isEmpty()) {
                tk.setMatKhau(passwordEncoder.encode(dto.getMatKhauMoi()));
                isTkChanged = true;
            }

            // Xử lý Upload Ảnh
            MultipartFile fileAnh = dto.getFileAnh();
            if (fileAnh != null && !fileAnh.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + fileAnh.getOriginalFilename().replaceAll("\\s+", "_");

                Path sourcePath = Paths.get("src/main/resources/static/images/avatar/" + fileName);
                Files.createDirectories(sourcePath.getParent());
                Files.copy(fileAnh.getInputStream(), sourcePath, StandardCopyOption.REPLACE_EXISTING);

                try {
                    Path targetPath = Paths.get("target/classes/static/images/avatar/" + fileName);
                    Files.createDirectories(targetPath.getParent());
                    Files.copy(fileAnh.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                } catch (Exception e) {
                    System.out.println("Thư mục target chưa sẵn sàng, bỏ qua copy.");
                }

                tk.setAnh(fileName);
                isTkChanged = true;
            }

            if (isTkChanged) {
                taiKhoanRepository.save(tk);
            }

            // 🔥 BƯỚC THẦN THÁNH: Làm mới Spring Security Session Context để Header nhận dữ liệu mới ngay lập tức
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            if (currentAuth != null) {
                // Gọi CustomUserDetailsService để load lại dữ liệu mới nhất vừa lưu từ DB lên
                UserDetails newPrincipal = customUserDetailsService.loadUserByUsername(username);

                // Tạo một Token Authentication mới chứa thông tin Principal mới
                Authentication newAuth = new UsernamePasswordAuthenticationToken(
                        newPrincipal,
                        currentAuth.getCredentials(),
                        newPrincipal.getAuthorities()
                );

                // Đè Token mới vào SecurityContextHolder của Session hiện tại
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            }
        }
    }
}