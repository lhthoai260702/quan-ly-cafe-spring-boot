package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.entity.Ban;
import com.quanlycafe.cafe_management.dto.ChiTietGoiMonDTO;
import com.quanlycafe.cafe_management.dto.ThongTinBanGoiMonDTO;
import com.quanlycafe.cafe_management.entity.ChiTietDatBan;
import com.quanlycafe.cafe_management.entity.HoaDon;
import com.quanlycafe.cafe_management.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class TablesService {

    @Autowired
    private BanRepository banRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private ChiTietDatBanRepository chiTietDatBanRepository;

    public Ban findById(Integer maBan) {
        return banRepository.findById(maBan).orElse(null);
    }

    public List<Ban> getAllTables() {
        return banRepository.findAll(Sort.by(Sort.Direction.ASC, "tenBan"));
    }

    public ThongTinBanGoiMonDTO getChiTietGoiMonTheoBan(Integer maBan) {
        // 1. Lấy thông tin cơ bản của bàn
        Ban ban = banRepository.findById(maBan).orElse(null);
        if (ban == null) return null;

        ThongTinBanGoiMonDTO dto = new ThongTinBanGoiMonDTO();
        dto.setMaBan(ban.getMaBan());
        dto.setTenBan(ban.getTenBan());
        dto.setTinhTrang(ban.getTinhTrang());

        // 2. Nếu bàn đang trống, không cần tìm hóa đơn
        if (!"Đang sử dụng".equalsIgnoreCase(ban.getTinhTrang())) {
            dto.setDanhSachMon(List.of());
            dto.setTongTien(0.0);
            return dto;
        }

        // 3. Tìm hóa đơn chưa thanh toán
        String sqlHoaDon = "SELECT hd.mahoadon, hd.tongtien FROM hoadon hd " +
                "JOIN chitietdatban ctdb ON hd.mahoadon = ctdb.mahoadon " +
                "WHERE ctdb.maban = ? AND hd.trangthai = 'Chưa thanh toán' LIMIT 1";

        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlHoaDon, maBan);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.get(0);

                // [SỬA LỖI Ở ĐÂY] Dùng Number thay vì Integer/BigDecimal để tránh lỗi ClassCastException
                Number maHoaDonDb = (Number) row.get("mahoadon");
                Integer maHoaDon = maHoaDonDb != null ? maHoaDonDb.intValue() : null;

                Number tongTienDb = (Number) row.get("tongtien");
                Double tongTien = tongTienDb != null ? tongTienDb.doubleValue() : 0.0;

                dto.setMaHoaDon(maHoaDon);
                dto.setTongTien(tongTien);

                // 4. Lấy danh sách món chi tiết từ hóa đơn này
                String sqlChiTiet = "SELECT td.tenmon, cthd.soluong, cthd.giataithoidiemban, cthd.thanhtien " +
                        "FROM chitiethoadon cthd " +
                        "JOIN thucdon td ON cthd.mathucdon = td.mathucdon " +
                        "WHERE cthd.mahoadon = ?";

                List<ChiTietGoiMonDTO> danhSachMon = jdbcTemplate.query(sqlChiTiet, (rs, rowNum) -> {
                    ChiTietGoiMonDTO item = new ChiTietGoiMonDTO();
                    item.setTenMon(rs.getString("tenmon"));
                    item.setSoLuong(rs.getInt("soluong"));

                    // [SỬA LỖI Ở ĐÂY] Dùng trực tiếp getDouble sẽ an toàn hơn việc getBigDecimal rồi .doubleValue()
                    item.setGiaTaiThoiDiemBan(rs.getDouble("giataithoidiemban"));
                    item.setThanhTien(rs.getDouble("thanhtien"));

                    return item;
                }, maHoaDon);

                dto.setDanhSachMon(danhSachMon);
            }
        } catch (Exception e) {
            e.printStackTrace();
            dto.setDanhSachMon(List.of());
            dto.setTongTien(0.0);
        }

        return dto;
    }

    @Transactional
    public boolean chuyenBan(Integer tuMaBan, Integer denMaBan) {
        // 1. Kiểm tra bàn
        Ban tuBan = banRepository.findById(tuMaBan).orElse(null);
        Ban denBan = banRepository.findById(denMaBan).orElse(null);

        if (tuBan == null || denBan == null ||
                !"Đang sử dụng".equalsIgnoreCase(tuBan.getTinhTrang()) ||
                !"Trống".equalsIgnoreCase(denBan.getTinhTrang())) {
            return false;
        }

        // 2. Chuyển hóa đơn sang bàn mới
        String sqlChuyen = "UPDATE chitietdatban SET maban = ? WHERE maban = ? AND mahoadon IN (SELECT mahoadon FROM hoadon WHERE trangthai = 'Chưa thanh toán')";
        int updated = jdbcTemplate.update(sqlChuyen, denMaBan, tuMaBan);

        if (updated > 0) {
            // 3. Đổi trạng thái 2 bàn
            tuBan.setTinhTrang("Trống");
            denBan.setTinhTrang("Đang sử dụng");
            banRepository.save(tuBan);
            banRepository.save(denBan);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean gopBan(List<Integer> tuMaBanList, Integer denMaBan) {
        try {
            // 1. Lấy mã hóa đơn của bàn đích
            String sqlLayHoaDon = "SELECT mahoadon FROM chitietdatban WHERE maban = ? AND mahoadon IN (SELECT mahoadon FROM hoadon WHERE trangthai = 'Chưa thanh toán') LIMIT 1";
            Integer maHoaDonDich = jdbcTemplate.queryForObject(sqlLayHoaDon, Integer.class, denMaBan);

            if (maHoaDonDich == null) return false;

            // 2. Duyệt qua từng bàn cần gộp
            for (Integer tuMaBan : tuMaBanList) {
                Integer maHoaDonNguon = jdbcTemplate.queryForObject(sqlLayHoaDon, Integer.class, tuMaBan);

                if (maHoaDonNguon != null) {
                    // Chuyển chi tiết hóa đơn (Cộng dồn nếu trùng món, Thêm mới nếu chưa có)
                    String sqlMergeChiTiet =
                            "INSERT INTO chitiethoadon (mathucdon, mahoadon, soluong, giataithoidiemban, thanhtien) " +
                                    "SELECT mathucdon, ?, soluong, giataithoidiemban, thanhtien FROM chitiethoadon WHERE mahoadon = ? " +
                                    "ON CONFLICT (mathucdon, mahoadon) DO UPDATE " +
                                    "SET soluong = chitiethoadon.soluong + EXCLUDED.soluong, " +
                                    "thanhtien = chitiethoadon.thanhtien + EXCLUDED.thanhtien";

                    jdbcTemplate.update(sqlMergeChiTiet, maHoaDonDich, maHoaDonNguon);

                    // Xóa hóa đơn nguồn và chi tiết đặt bàn của bàn nguồn
                    jdbcTemplate.update("DELETE FROM chitietdatban WHERE mahoadon = ?", maHoaDonNguon);
                    jdbcTemplate.update("DELETE FROM hoadon WHERE mahoadon = ?", maHoaDonNguon);
                }

                // Cập nhật trạng thái bàn nguồn thành Trống
                jdbcTemplate.update("UPDATE ban SET tinhtrang = 'Trống' WHERE maban = ?", tuMaBan);
            }

            // Cập nhật lại tổng tiền cho hóa đơn đích
            String sqlUpdateTongTien = "UPDATE hoadon SET tongtien = (SELECT COALESCE(SUM(thanhtien), 0) FROM chitiethoadon WHERE mahoadon = ?) WHERE mahoadon = ?";
            jdbcTemplate.update(sqlUpdateTongTien, maHoaDonDich, maHoaDonDich);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Map<String, Object>> getDanhSachMonJsonTheoBan(Integer maBan) {
        String sql = "SELECT cthd.mathucdon, td.tenmon, cthd.soluong, cthd.giataithoidiemban " +
                "FROM chitiethoadon cthd " +
                "JOIN thucdon td ON cthd.mathucdon = td.mathucdon " +
                "JOIN chitietdatban ctdb ON cthd.mahoadon = ctdb.mahoadon " +
                "JOIN hoadon hd ON hd.mahoadon = ctdb.mahoadon " +
                "WHERE ctdb.maban = ? AND hd.trangthai = 'Chưa thanh toán'";
        return jdbcTemplate.queryForList(sql, maBan);
    }

    @Transactional
    public boolean tachBan(Integer tuMaBan, Integer denMaBan, List<Integer> mathucdonList, List<Integer> soluongTachList) {
        try {
            // 1. Tìm hóa đơn chưa thanh toán của bàn cũ
            String sqlLayThongTin = "SELECT mahoadon, manhanvien, tenkhachhang, sdtkhachhang, ngaygiodat " +
                    "FROM chitietdatban WHERE maban = ? AND mahoadon IN " +
                    "(SELECT mahoadon FROM hoadon WHERE trangthai = 'Chưa thanh toán') LIMIT 1";
            Map<String, Object> thongTinCu = jdbcTemplate.queryForMap(sqlLayThongTin, tuMaBan);

            if (thongTinCu == null || thongTinCu.isEmpty()) return false;

            Integer maHoaDonCu = (Integer) thongTinCu.get("mahoadon");
            Integer maNhanVien = (Integer) thongTinCu.get("manhanvien");
            String tenKhachHang = (String) thongTinCu.get("tenkhachhang");
            String sdtKhachHang = (String) thongTinCu.get("sdtkhachhang");
            Object ngayGioDat = thongTinCu.get("ngaygiodat");

            // 2. Tạo một Hóa Đơn Mới tinh cho bàn đích
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                // CHỈ ĐỊNH RÕ RÀNG chỉ trả về cột "mahoadon"
                PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO hoadon (tongtien, trangthai) VALUES (0, 'Chưa thanh toán')",
                        new String[] { "mahoadon" }
                );
                return ps;
            }, keyHolder);

            // Ép kiểu an toàn từ Map trả về
            Map<String, Object> keys = keyHolder.getKeys();
            Integer maHoaDonMoi = null;
            if (keys != null && keys.get("mahoadon") != null) {
                maHoaDonMoi = ((Number) keys.get("mahoadon")).intValue();
            }
            if (maHoaDonMoi == null) return false;

            // 3. Đưa bàn mới vào bảng liên kết chi tiết đặt bàn
            jdbcTemplate.update(
                    "INSERT INTO chitietdatban (maban, mahoadon, manhanvien, tenkhachhang, sdtkhachhang, ngaygiodat) VALUES (?, ?, ?, ?, ?, ?)",
                    denMaBan, maHoaDonMoi, maNhanVien, tenKhachHang, sdtKhachHang, ngayGioDat
            );

            // 4. Bắt đầu lặp qua danh sách món để tách số lượng dữ liệu gửi lên
            for (int i = 0; i < mathucdonList.size(); i++) {
                Integer maThucDon = mathucdonList.get(i);
                Integer slTach = soluongTachList.get(i);

                if (slTach == null || slTach <= 0) continue;

                // Lấy thông tin dòng hóa đơn hiện tại để kiểm tra tính hợp lệ
                Map<String, Object> cthdCu = jdbcTemplate.queryForMap(
                        "SELECT soluong, giataithoidiemban FROM chitiethoadon WHERE mahoadon = ? AND mathucdon = ?",
                        maHoaDonCu, maThucDon
                );
                int slHienTai = (int) cthdCu.get("soluong");
                BigDecimal giaBan = (BigDecimal) cthdCu.get("giataithoidiemban");

                if (slTach > slHienTai) return false; // Ngăn chặn phá hoại dữ liệu

                // Thêm món được chọn tách sang hóa đơn mới
                double thanhTienMoi = slTach * giaBan.doubleValue();
                jdbcTemplate.update(
                        "INSERT INTO chitiethoadon (mathucdon, mahoadon, soluong, giataithoidiemban, thanhtien) VALUES (?, ?, ?, ?, ?)",
                        maThucDon, maHoaDonMoi, slTach, giaBan, thanhTienMoi
                );

                // Cập nhật lại số lượng ở hóa đơn cũ
                int slConLai = slHienTai - slTach;
                if (slConLai == 0) {
                    jdbcTemplate.update("DELETE FROM chitiethoadon WHERE mahoadon = ? AND mathucdon = ?", maHoaDonCu, maThucDon);
                } else {
                    double thanhTienConLai = slConLai * giaBan.doubleValue();
                    jdbcTemplate.update(
                            "UPDATE chitiethoadon SET soluong = ?, thanhtien = ? WHERE mahoadon = ? AND mathucdon = ?",
                            slConLai, thanhTienConLai, maHoaDonCu, maThucDon
                    );
                }
            }

            // 5. Đồng bộ cập nhật lại Tổng Tiền cuối cùng cho cả 2 hóa đơn
            String sqlUpdateTongTien = "UPDATE hoadon SET tongtien = (SELECT COALESCE(SUM(thanhtien), 0) FROM chitiethoadon WHERE mahoadon = ?) WHERE mahoadon = ?";
            jdbcTemplate.update(sqlUpdateTongTien, maHoaDonCu, maHoaDonCu);
            jdbcTemplate.update(sqlUpdateTongTien, maHoaDonMoi, maHoaDonMoi);

            // 6. Cập nhật bàn mới sang trạng thái 'Đang sử dụng'
            jdbcTemplate.update("UPDATE ban SET tinhtrang = 'Đang sử dụng' WHERE maban = ?", denMaBan);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public void datBanTruoc(Integer maBan, Integer maNhanVien, String tenKhachHang, String sdtKhachHang, LocalDateTime ngayGioDat) {
        // 1. Tạo mới hóa đơn
        HoaDon hoaDonMoi = new HoaDon();
        hoaDonMoi.setTrangThai("Chưa thanh toán");
        hoaDonMoi.setTongTien(0.0);
        hoaDonMoi = hoaDonRepository.save(hoaDonMoi);

        // 2. Tạo thông tin Chi Tiết Đặt Bàn
        ChiTietDatBan datBan = new ChiTietDatBan();
        datBan.setMaBan(maBan);
        datBan.setMaHoaDon(hoaDonMoi.getMaHoaDon());
        datBan.setTenKhachHang(tenKhachHang);
        datBan.setSdtKhachHang(sdtKhachHang);
        datBan.setNgayGioDat(ngayGioDat);

        // SỬ DỤNG MÃ NHÂN VIÊN TỪ CONTROLLER TRUYỀN XUỐNG
        datBan.setMaNhanVien(maNhanVien);

        chiTietDatBanRepository.save(datBan);

        // 3. Đổi trạng thái bàn
        Ban ban = banRepository.findById(maBan)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn yêu cầu"));
        ban.setTinhTrang("Đã đặt trước");
        banRepository.save(ban);
    }
}