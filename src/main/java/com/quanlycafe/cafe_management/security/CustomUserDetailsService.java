package com.quanlycafe.cafe_management.security;

import com.quanlycafe.cafe_management.entity.TaiKhoan;
import com.quanlycafe.cafe_management.repository.TaiKhoanRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * CustomUserDetailsService
 * <p>
 * Version 1.2
 * <p>
 * Date: 29-05-2026
 * <p>
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 * 07-06-2026 lhthoai      Check flag_delete during login
 * 07-06-2026 lhthoai      Standardize Java Coding Convention
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final TaiKhoanRepository taiKhoanRepository;

    /**
     * Khởi tạo CustomUserDetailsService
     *
     * @param taiKhoanRepository TaiKhoanRepository
     */
    public CustomUserDetailsService(TaiKhoanRepository taiKhoanRepository) {
        this.taiKhoanRepository = taiKhoanRepository;
    }

    /**
     * Tải thông tin người dùng theo tên đăng nhập và kiểm tra trạng thái xóa mềm
     *
     * @param username String
     * @return UserDetails
     * @throws UsernameNotFoundException khi không tìm thấy tài khoản hoặc tài khoản đã bị xóa
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String usernameLower = username.toLowerCase().trim();

        TaiKhoan taiKhoan = taiKhoanRepository.findByTenDangNhap(usernameLower)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản: " + username));

        // Kiểm tra trạng thái xóa mềm (flagDelete == 1 là đã bị xóa)
        if (taiKhoan.getFlagDelete() != null && taiKhoan.getFlagDelete() == 1) {
            throw new UsernameNotFoundException("Không tìm thấy tài khoản: " + username);
        }

        String role = (taiKhoan.getQuyenHan() == 1) ? "ROLE_ADMIN" : "ROLE_USER";

        return new User(
                taiKhoan.getTenDangNhap(),
                taiKhoan.getMatKhau(),
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}