package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.dto.EmployeeFormDTO;
import com.quanlycafe.cafe_management.dto.UserProfileDTO;
import com.quanlycafe.cafe_management.entity.ChucVu;
import com.quanlycafe.cafe_management.entity.NhanVien;
import com.quanlycafe.cafe_management.entity.TaiKhoan;
import com.quanlycafe.cafe_management.repository.ChucVuRepository;
import com.quanlycafe.cafe_management.repository.NhanVienRepository;
import com.quanlycafe.cafe_management.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * EmployeeService
 * * Version 1.1
 * * Date: 29-05-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 30-05-2026 lthoai      Format convention, apply EmployeeFormDTO
 * 30-05-2026 lthoai      apply pagination (Pageable)
 */
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final ChucVuRepository chucVuRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Hiển thị tất cả nhân viên (có phân trang)
     *
     * @param pageable Pageable
     * @return Page<UserProfileDTO>
     */
    public Page<UserProfileDTO> getAllEmployees(Pageable pageable) {
        Page<NhanVien> pageNhanVien = nhanVienRepository.findAll(pageable);
        return pageNhanVien.map(this::mapToDTO);
    }

    /**
     * Lọc nhân viên theo chức vụ (có phân trang)
     *
     * @param roleType String
     * @param pageable Pageable
     * @return Page<UserProfileDTO>
     */
    public Page<UserProfileDTO> getEmployeesByRoleType(String roleType, Pageable pageable) {
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
                return getAllEmployees(pageable);
        }

        Page<NhanVien> pageNhanVien = nhanVienRepository.findByChucVu_TenChucVuContainingIgnoreCase(keyword, pageable);
        return pageNhanVien.map(this::mapToDTO);
    }

    /**
     * Tìm kiếm nhân viên (có phân trang)
     *
     * @param keyword  String
     * @param pageable Pageable
     * @return Page<UserProfileDTO>
     */
    public Page<UserProfileDTO> searchEmployees(String keyword, Pageable pageable) {
        Page<NhanVien> pageNhanVien = nhanVienRepository
                .findByHoTenContainingIgnoreCaseOrTaiKhoan_TenDangNhapContainingIgnoreCase(keyword, keyword, pageable);
        return pageNhanVien.map(this::mapToDTO);
    }

    /**
     * Thêm nhân viên
     *
     * @param form EmployeeFormDTO
     */
    @Transactional
    public void createEmployee(EmployeeFormDTO form) {
        // 1. Tạo tài khoản trước
        TaiKhoan tk = new TaiKhoan();
        tk.setTenDangNhap(form.getTenDangNhap());
        tk.setMatKhau(passwordEncoder.encode(form.getMatKhau()));
        tk.setAnh("user.png");

        tk.setQuyenHan(form.getMaChucVu() == 1 ? 1 : 2);
        taiKhoanRepository.save(tk);

        // 2. Lấy chức vụ từ DB
        ChucVu cv = chucVuRepository.findById(form.getMaChucVu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chức vụ"));

        // 3. Tạo nhân viên và liên kết khóa ngoại
        NhanVien nv = new NhanVien();
        nv.setHoTen(form.getHoTen());
        nv.setSoDienThoai(form.getSoDienThoai());
        nv.setDiaChi(form.getDiaChi());
        nv.setTaiKhoan(tk);
        nv.setChucVu(cv);

        nhanVienRepository.save(nv);
    }

    /**
     * Cập nhật thông tin nhân viên
     *
     * @param form EmployeeFormDTO
     */
    @Transactional
    public void updateEmployee(EmployeeFormDTO form) {
        NhanVien nv = nhanVienRepository.findById(form.getMaNhanVien())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        nv.setHoTen(form.getHoTen());
        nv.setSoDienThoai(form.getSoDienThoai());
        nv.setDiaChi(form.getDiaChi());

        if (form.getMaChucVu() != null) {
            ChucVu cv = chucVuRepository.findById(form.getMaChucVu())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chức vụ"));
            nv.setChucVu(cv);

            TaiKhoan tk = nv.getTaiKhoan();
            if (tk != null) {
                tk.setQuyenHan(form.getMaChucVu() == 1 ? 1 : 2);
                taiKhoanRepository.save(tk);
            }
        }
        nhanVienRepository.save(nv);
    }

    /**
     * Xoá nhân viên
     *
     * @param maNhanVien Integer
     */
    @Transactional
    public void deleteEmployee(Integer maNhanVien) {
        NhanVien nv = nhanVienRepository.findById(maNhanVien)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên cần xóa"));

        TaiKhoan tk = nv.getTaiKhoan();
        nhanVienRepository.delete(nv);

        if (tk != null) {
            taiKhoanRepository.delete(tk);
        }
    }

    /**
     * Chuyển đổi Entity sang DTO
     *
     * @param nv NhanVien
     * @return UserProfileDTO
     */
    private UserProfileDTO mapToDTO(NhanVien nv) {
        UserProfileDTO dto = new UserProfileDTO();
        dto.setMaNhanVien(nv.getMaNhanVien());
        dto.setHoTen(nv.getHoTen());
        dto.setSoDienThoai(nv.getSoDienThoai());
        dto.setDiaChi(nv.getDiaChi());

        if (nv.getChucVu() != null) {
            dto.setTenChucVu(nv.getChucVu().getTenChucVu());
            dto.setLuong(nv.getChucVu().getLuong());
            dto.setMaChucVu(nv.getChucVu().getMaChucVu());
        }

        if (nv.getTaiKhoan() != null) {
            dto.setTenDangNhap(nv.getTaiKhoan().getTenDangNhap());
            dto.setQuyenHan(nv.getTaiKhoan().getQuyenHan());
            dto.setAnh(nv.getTaiKhoan().getAnh());
        }
        return dto;
    }
}