package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.dto.UserProfileDTO;
import com.quanlycafe.cafe_management.entity.NhanVien;
import com.quanlycafe.cafe_management.entity.TaiKhoan;
import com.quanlycafe.cafe_management.repository.NhanVienRepository;
import com.quanlycafe.cafe_management.repository.TaiKhoanRepository;
import com.quanlycafe.cafe_management.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
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
 * Version 1.5
 * Date: 09-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 29-05-2026   lhthoai     Create
 * 30-05-2026   lhthoai     Add PasswordEncoder & format by convention
 * 08-06-2026   lhthoai     Add Avatar upload logic in updateProfile
 * 09-06-2026   lhthoai     Fix validation matKhauMoi & Thêm logic reload Security Context
 * 09-06-2026   lhthoai     Apply Java Coding Convention & Refactor to Constructor Injection
 */
@Service
@RequiredArgsConstructor
public class ProfileService {

    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Lấy thông tin hồ sơ của người dùng đang đăng nhập.
     *
     * @return DTO chứa thông tin profile người dùng
     */
    public UserProfileDTO getCurrentUserProfile() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails) ? ((UserDetails) principal).getUsername() : principal.toString();

        UserProfileDTO dto = new UserProfileDTO();

        // Lấy thông tin Tài khoản
        Optional<TaiKhoan> taiKhoanOpt = taiKhoanRepository.findByTenDangNhap(username);
        taiKhoanOpt.ifPresent(tk -> {
            dto.setTenDangNhap(tk.getTenDangNhap());
            dto.setQuyenHan(tk.getQuyenHan());
            dto.setAnh(tk.getAnh() != null ? tk.getAnh() : "user.png");
        });

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
     * Cập nhật thông tin profile, mật khẩu và ảnh đại diện.
     *
     * @param dto DTO chứa dữ liệu cập nhật
     * @throws Exception Ngoại lệ khi cập nhật thông tin
     */
    @Transactional
    public void updateProfile(UserProfileDTO dto) throws Exception {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = (principal instanceof UserDetails) ? ((UserDetails) principal).getUsername() : principal.toString();

        // 1. Cập nhật thông tin nhân viên
        NhanVien nv = nhanVienRepository.findByTaiKhoan_TenDangNhap(username)
                .orElseThrow(() -> new Exception("Không tìm thấy thông tin nhân viên!"));
        nv.setHoTen(dto.getHoTen());
        nv.setSoDienThoai(dto.getSoDienThoai());
        nhanVienRepository.save(nv);

        // 2. Cập nhật Mật khẩu và Ảnh đại diện trong bảng TaiKhoan
        TaiKhoan tk = taiKhoanRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new Exception("Không tìm thấy tài khoản!"));

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

            // Bỏ qua lỗi nếu thư mục target chưa tồn tại (trong quá trình phát triển)
            try {
                Path targetPath = Paths.get("target/classes/static/images/avatar/" + fileName);
                Files.createDirectories(targetPath.getParent());
                Files.copy(fileAnh.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (Exception e) {
                // Log silently
            }

            tk.setAnh(fileName);
            isTkChanged = true;
        }

        if (isTkChanged) {
            taiKhoanRepository.save(tk);
            refreshSecurityContext(username, tk.getMatKhau());
        }
    }

    /**
     * Tự động làm mới Spring Security Session Context sau khi đổi dữ liệu.
     *
     * @param username Tên đăng nhập
     * @param password Mật khẩu đã mã hóa
     */
    private void refreshSecurityContext(String username, String password) {
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        if (currentAuth != null) {
            UserDetails newPrincipal = customUserDetailsService.loadUserByUsername(username);
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    newPrincipal,
                    currentAuth.getCredentials(),
                    newPrincipal.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);
        }
    }
}