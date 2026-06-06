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

import java.util.List;

/**
 * EmployeeService
 * * Version 1.4
 * * Date: 04-06-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 04-06-2026 lthoai      Standardize Java Convention & Dynamic Roles
 * 04-06-2026 lthoai      Add Director Role & Update Permission Logic
 */
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final ChucVuRepository chucVuRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Lấy danh sách tất cả chức vụ từ Database để đổ ra dropdown
     *
     * @return List<ChucVu>
     */
    public List<ChucVu> getAllChucVu() {
        return chucVuRepository.findAll();
    }

    /**
     * Lấy danh sách tất cả nhân viên có phân trang
     *
     * @param pageable Pageable
     * @return Page<UserProfileDTO>
     */
    public Page<UserProfileDTO> getAllEmployees(Pageable pageable) {
        Page<NhanVien> pageNhanVien = nhanVienRepository.findAll(pageable);
        return pageNhanVien.map(this::mapToDTO);
    }

    /**
     * Lọc nhân viên theo chức vụ (Cập nhật 4 loại)
     *
     * @param roleType String
     * @param pageable Pageable
     * @return Page<UserProfileDTO>
     */
    public Page<UserProfileDTO> getEmployeesByRoleType(String roleType, Pageable pageable) {
        String keyword = "";

        switch (roleType) {
            case "giamdoc":
                keyword = "Giám Đốc";
                break;
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
     * Tìm kiếm nhân viên theo từ khóa (họ tên hoặc tên đăng nhập)
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
     * Thêm mới nhân viên và tài khoản
     *
     * @param form EmployeeFormDTO
     */
    @Transactional
    public void createEmployee(EmployeeFormDTO form) {
        ChucVu cv = chucVuRepository.findById(form.getMaChucVu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chức vụ"));

        TaiKhoan tk = new TaiKhoan();
        tk.setTenDangNhap(form.getTenDangNhap().toLowerCase());
        tk.setMatKhau(passwordEncoder.encode(form.getMatKhau()));
        tk.setAnh("user.png");

        // Nếu tên chức vụ chứa chữ "Giám đốc" hoặc "Quản lý" thì cấp quyền Admin (1)
        boolean isAdmin = cv.getTenChucVu().toLowerCase().contains("giám đốc") ||
                cv.getTenChucVu().toLowerCase().contains("quản lý");
        tk.setQuyenHan(isAdmin ? 1 : 2);

        taiKhoanRepository.save(tk);

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
                // Cập nhật lại quyền nếu lỡ đổi chức vụ
                boolean isAdmin = cv.getTenChucVu().toLowerCase().contains("giám đốc") ||
                        cv.getTenChucVu().toLowerCase().contains("quản lý");
                tk.setQuyenHan(isAdmin ? 1 : 2);
                taiKhoanRepository.save(tk);
            }
        }

        nhanVienRepository.save(nv);
    }

    /**
     * Xóa nhân viên và tài khoản liên kết
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
     * Chuyển đổi dữ liệu Entity sang DTO
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