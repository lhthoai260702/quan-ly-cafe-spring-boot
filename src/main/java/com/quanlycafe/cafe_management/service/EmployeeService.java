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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * EmployeeService
 * Version 1.9
 * Date: 12-06-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai      Create
 * 04-06-2026 lhthoai      Standardize Java Convention & Dynamic Roles
 * 07-06-2026 lhthoai      Dynamic database role filtering, map and save NhanVien Luong
 * 07-06-2026 lhthoai      Standardize imports and Javadoc comments
 * 07-06-2026 lhthoai      Apply Soft Delete for TaiKhoan
 * 12-06-2026 Quản Lý Cafe Cập nhật tính năng sửa cả tài khoản & mật khẩu (Có check trùng)
 */
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final ChucVuRepository chucVuRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Lấy danh sách tất cả chức vụ từ Database để đổ ra dropdown.
     *
     * @return Danh sách các đối tượng ChucVu
     */
    public List<ChucVu> getAllChucVu() {
        return chucVuRepository.findAll();
    }

    /**
     * Lấy danh sách tất cả nhân viên có phân trang.
     *
     * @param pageable Đối tượng phân trang
     * @return Trang chứa danh sách UserProfileDTO
     */
    public Page<UserProfileDTO> getAllEmployees(Pageable pageable) {
        Page<NhanVien> pageNhanVien = nhanVienRepository.findAll(pageable);
        return pageNhanVien.map(this::mapToDTO);
    }

    /**
     * Lọc danh sách nhân viên theo mã chức vụ.
     *
     * @param roleId   Mã chức vụ cần lọc
     * @param pageable Đối tượng phân trang
     * @return Trang chứa danh sách UserProfileDTO đã lọc
     */
    public Page<UserProfileDTO> getEmployeesByRoleId(Integer roleId, Pageable pageable) {
        Page<NhanVien> pageNhanVien = nhanVienRepository.findByChucVu_MaChucVu(roleId, pageable);
        return pageNhanVien.map(this::mapToDTO);
    }

    /**
     * Tìm kiếm nhân viên theo họ tên hoặc tên đăng nhập.
     *
     * @param keyword  Từ khóa tìm kiếm
     * @param pageable Đối tượng phân trang
     * @return Trang chứa danh sách UserProfileDTO tìm được
     */
    public Page<UserProfileDTO> searchEmployees(String keyword, Pageable pageable) {
        Page<NhanVien> pageNhanVien = nhanVienRepository
                .findByHoTenContainingIgnoreCaseOrTaiKhoan_TenDangNhapContainingIgnoreCase(keyword, keyword, pageable);
        return pageNhanVien.map(this::mapToDTO);
    }

    /**
     * Tạo mới nhân viên và cấp tài khoản tương ứng.
     *
     * @param form Form chứa dữ liệu nhân viên từ View
     */
    @Transactional
    public void createEmployee(EmployeeFormDTO form) {
        if (taiKhoanRepository.existsByTenDangNhapIgnoreCase(form.getTenDangNhap().trim())) {
            throw new IllegalArgumentException("Tên đăng nhập này đã có người sử dụng. Vui lòng chọn tên khác!");
        }

        ChucVu cv = chucVuRepository.findById(form.getMaChucVu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chức vụ"));

        TaiKhoan tk = new TaiKhoan();
        tk.setTenDangNhap(form.getTenDangNhap().toLowerCase().trim());
        tk.setMatKhau(passwordEncoder.encode(form.getMatKhau()));
        tk.setAnh("user.png");

        boolean isAdmin = cv.getTenChucVu().toLowerCase().contains("giám đốc") ||
                cv.getTenChucVu().toLowerCase().contains("quản lý");
        tk.setQuyenHan(isAdmin ? 1 : 2);

        // Gán cờ bằng 0 cho tài khoản mới (Chưa xóa)
        tk.setFlagDelete(0);

        taiKhoanRepository.save(tk);

        NhanVien nv = new NhanVien();
        nv.setHoTen(form.getHoTen());
        nv.setSoDienThoai(form.getSoDienThoai());
        nv.setDiaChi(form.getDiaChi());
        nv.setLuong(form.getLuong());
        nv.setTaiKhoan(tk);
        nv.setChucVu(cv);

        // Gán cờ bằng 0 cho nhân viên mới (Chưa xóa)
        nv.setFlagDelete(0);

        nhanVienRepository.save(nv);
    }

    /**
     * Cập nhật thông tin nhân viên và tài khoản.
     * Cho phép đổi Tên đăng nhập và Mật khẩu (Có check trùng tên).
     *
     * @param form Form chứa dữ liệu nhân viên từ View
     */
    @Transactional
    public void updateEmployee(EmployeeFormDTO form) {
        NhanVien nv = nhanVienRepository.findById(form.getMaNhanVien())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        nv.setHoTen(form.getHoTen());
        nv.setSoDienThoai(form.getSoDienThoai());
        nv.setDiaChi(form.getDiaChi());
        nv.setLuong(form.getLuong());

        if (form.getMaChucVu() != null) {
            ChucVu cv = chucVuRepository.findById(form.getMaChucVu())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chức vụ"));
            nv.setChucVu(cv);

            TaiKhoan tk = nv.getTaiKhoan();
            if (tk != null) {
                // 1. CẬP NHẬT TÊN ĐĂNG NHẬP (Kiểm tra trùng chéo)
                if (form.getTenDangNhap() != null && !form.getTenDangNhap().trim().isEmpty()) {
                    String newUsername = form.getTenDangNhap().toLowerCase().trim();
                    // Nếu tên đăng nhập mới khác tên đăng nhập cũ, cần kiểm tra xem có bị trùng với người khác không
                    if (!tk.getTenDangNhap().equals(newUsername)) {
                        if (taiKhoanRepository.existsByTenDangNhapIgnoreCase(newUsername)) {
                            throw new IllegalArgumentException("Tên đăng nhập '" + newUsername + "' đã được sử dụng!");
                        }
                        tk.setTenDangNhap(newUsername);
                    }
                }

                // 2. CẬP NHẬT MẬT KHẨU (Chỉ update nếu có nhập pass mới)
                if (form.getMatKhau() != null && !form.getMatKhau().trim().isEmpty()) {
                    tk.setMatKhau(passwordEncoder.encode(form.getMatKhau()));
                }

                // 3. CẬP NHẬT QUYỀN HẠN DỰA THEO CHỨC VỤ
                boolean isAdmin = cv.getTenChucVu().toLowerCase().contains("giám đốc") ||
                        cv.getTenChucVu().toLowerCase().contains("quản lý");
                tk.setQuyenHan(isAdmin ? 1 : 2);

                taiKhoanRepository.save(tk);
            }
        }

        nhanVienRepository.save(nv);
    }

    /**
     * Xóa nhân viên theo mã nhân viên (Xóa mềm).
     *
     * @param maNhanVien Mã nhân viên cần xóa
     * @return true nếu người dùng tự xóa chính mình, ngược lại false
     */
    @Transactional
    public boolean deleteEmployee(Integer maNhanVien) {
        NhanVien nv = nhanVienRepository.findById(maNhanVien)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên cần xóa"));

        TaiKhoan tk = nv.getTaiKhoan();
        boolean isSelfDeleted = false;

        // Kiểm tra tài khoản đang đăng nhập
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String loggedInUsername = ((UserDetails) principal).getUsername();
            if (tk != null && tk.getTenDangNhap().equals(loggedInUsername)) {
                isSelfDeleted = true;
            }
        }

        // Thực hiện xóa mềm
        nv.setFlagDelete(1);
        nhanVienRepository.save(nv);

        if (tk != null) {
            tk.setFlagDelete(1);
            taiKhoanRepository.save(tk);
        }

        return isSelfDeleted;
    }

    /**
     * Chuyển đổi đối tượng NhanVien sang UserProfileDTO để hiển thị.
     *
     * @param nv Đối tượng NhanVien
     * @return Đối tượng UserProfileDTO tương ứng
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