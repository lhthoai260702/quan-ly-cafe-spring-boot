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
 * <p>
 * Version 1.7
 * <p>
 * Date: 07-06-2026
 * <p>
 * Copyright
 * <p>
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 04-06-2026 lthoai       Standardize Java Convention & Dynamic Roles
 * 07-06-2026 Quản Lý      Dynamic database role filtering, map and save NhanVien Luong
 * 07-06-2026 Quản Lý      Standardize imports and Javadoc comments
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
     * Lọc danh sách nhân viên theo mã chức vụ
     *
     * @param roleId   Integer
     * @param pageable Pageable
     * @return Page<UserProfileDTO>
     */
    public Page<UserProfileDTO> getEmployeesByRoleId(Integer roleId, Pageable pageable) {
        Page<NhanVien> pageNhanVien = nhanVienRepository.findByChucVu_MaChucVu(roleId, pageable);
        return pageNhanVien.map(this::mapToDTO);
    }

    /**
     * Tìm kiếm nhân viên theo họ tên hoặc tên đăng nhập
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
     * Tạo mới nhân viên và cấp tài khoản tương ứng
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

        boolean isAdmin = cv.getTenChucVu().toLowerCase().contains("giám đốc") ||
                cv.getTenChucVu().toLowerCase().contains("quản lý");
        tk.setQuyenHan(isAdmin ? 1 : 2);

        taiKhoanRepository.save(tk);

        NhanVien nv = new NhanVien();
        nv.setHoTen(form.getHoTen());
        nv.setSoDienThoai(form.getSoDienThoai());
        nv.setDiaChi(form.getDiaChi());
        nv.setLuong(form.getLuong()); // LƯU LƯƠNG NHÂN VIÊN VÀO DB
        nv.setTaiKhoan(tk);
        nv.setChucVu(cv);

        nhanVienRepository.save(nv);
    }

    /**
     * Cập nhật thông tin nhân viên và phân quyền lại nếu chức vụ thay đổi
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
        nv.setLuong(form.getLuong()); // CẬP NHẬT LƯƠNG NHÂN VIÊN

        if (form.getMaChucVu() != null) {
            ChucVu cv = chucVuRepository.findById(form.getMaChucVu())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chức vụ"));
            nv.setChucVu(cv);

            TaiKhoan tk = nv.getTaiKhoan();
            if (tk != null) {
                boolean isAdmin = cv.getTenChucVu().toLowerCase().contains("giám đốc") ||
                        cv.getTenChucVu().toLowerCase().contains("quản lý");
                tk.setQuyenHan(isAdmin ? 1 : 2);
                taiKhoanRepository.save(tk);
            }
        }

        nhanVienRepository.save(nv);
    }

    /**
     * Xóa hồ sơ nhân viên và tài khoản liên kết
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
     * Chuyển đổi đối tượng NhanVien sang UserProfileDTO để hiển thị
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
        dto.setLuong(nv.getLuong());

        if (nv.getChucVu() != null) {
            dto.setTenChucVu(nv.getChucVu().getTenChucVu());
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