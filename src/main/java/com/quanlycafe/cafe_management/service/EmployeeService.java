package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.dto.UserProfileDTO;
import com.quanlycafe.cafe_management.entity.ChucVu;
import com.quanlycafe.cafe_management.entity.NhanVien;
import com.quanlycafe.cafe_management.entity.TaiKhoan;
import com.quanlycafe.cafe_management.repository.ChucVuRepository;
import com.quanlycafe.cafe_management.repository.NhanVienRepository;
import com.quanlycafe.cafe_management.repository.TaiKhoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * EmployeeService
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 */
@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final NhanVienRepository nhanVienRepository;
    private final TaiKhoanRepository taiKhoanRepository;
    private final ChucVuRepository chucVuRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Hiển thị tất cả nhân viên
     *
     * @return List<UserProfileDTO>
     */
    public List<UserProfileDTO> getAllEmployees() {
        List<NhanVien> danhSachNhanVien = nhanVienRepository.findAll();
        return danhSachNhanVien.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Tiếp nhận bộ lọc 3 phần từ Controller gửi xuống
     *
     * @param roleType String
     * @return List<UserProfileDTO>
     */
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

    /**
     * Lấy thông tin nhân viên
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

    /**
     * Search thông tin
     *
     * @param keyword String
     * @return List<UserProfileDTO>
     */
    public List<UserProfileDTO> searchEmployees(String keyword) {
        return nhanVienRepository.findByHoTenContainingIgnoreCaseOrTaiKhoan_TenDangNhapContainingIgnoreCase(keyword, keyword)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Thêm nhân viên
     *
     * @param hoTen       String
     * @param soDienThoai String
     * @param diaChi      String
     * @param maChucVu    Integer
     * @param tenDangNhap String
     * @param matKhau     String
     */
    @Transactional
    public void createEmployee(String hoTen, String soDienThoai, String diaChi, Integer maChucVu, String tenDangNhap, String matKhau) {
        // 1. Tạo tài khoản trước
        TaiKhoan tk = new TaiKhoan();
        tk.setTenDangNhap(tenDangNhap);
        tk.setMatKhau(passwordEncoder.encode(matKhau)); // Mã hóa mật khẩu
        tk.setAnh("user.png"); // Ảnh mặc định

        // Set quyền hạn dựa theo chức vụ (Ví dụ: 1 là Quản lý có quyền 1, còn lại quyền 2)
        tk.setQuyenHan(maChucVu == 1 ? 1 : 2);
        taiKhoanRepository.save(tk);

        // 2. Lấy chức vụ từ DB
        ChucVu cv = chucVuRepository.findById(maChucVu)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chức vụ"));

        // 3. Tạo nhân viên và liên kết khóa ngoại
        NhanVien nv = new NhanVien();
        nv.setHoTen(hoTen);
        nv.setSoDienThoai(soDienThoai);
        nv.setDiaChi(diaChi);
        nv.setTaiKhoan(tk); // Liên kết tài khoản vừa tạo
        nv.setChucVu(cv);   // Liên kết chức vụ

        nhanVienRepository.save(nv);
    }

    /**
     * Cập nhật thông tin nhân viên
     *
     * @param maNhanVien  Integer
     * @param hoTen       String
     * @param soDienThoai String
     * @param diaChi      String
     * @param maChucVu    Integer
     */
    @Transactional
    public void updateEmployee(Integer maNhanVien, String hoTen, String soDienThoai, String diaChi, Integer maChucVu) {
        // 1. Tìm nhân viên hiện tại
        NhanVien nv = nhanVienRepository.findById(maNhanVien)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        // 2. Cập nhật thông tin cá nhân
        nv.setHoTen(hoTen);
        nv.setSoDienThoai(soDienThoai);
        nv.setDiaChi(diaChi);

        // 3. Cập nhật chức vụ và quyền hạn (nếu có thay đổi)
        if (maChucVu != null) {
            ChucVu cv = chucVuRepository.findById(maChucVu)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chức vụ"));
            nv.setChucVu(cv);

            TaiKhoan tk = nv.getTaiKhoan();
            if (tk != null) {
                // Ví dụ: maChucVu = 1 là Quản lý -> quyền 1. Còn lại là quyền 2.
                tk.setQuyenHan(maChucVu == 1 ? 1 : 2);
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
        // 1. Kiểm tra nhân viên có tồn tại hay không
        NhanVien nv = nhanVienRepository.findById(maNhanVien)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên cần xóa"));

        // Lưu lại thông tin tài khoản trước khi xóa thực thể NhanVien
        TaiKhoan tk = nv.getTaiKhoan();

        // 2. Xóa nhân viên trước (để ngắt liên kết khóa ngoại tham chiếu đến TaiKhoan)
        nhanVienRepository.delete(nv);

        // 3. Nếu nhân viên này có tài khoản, tiến hành xóa tài khoản của họ
        if (tk != null) {
            taiKhoanRepository.delete(tk);
        }
    }
}