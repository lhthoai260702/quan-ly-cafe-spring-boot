package com.quanlycafe.cafe_management.service;

import com.quanlycafe.cafe_management.dto.ChiTietGoiMonDTO;
import com.quanlycafe.cafe_management.dto.ThongTinBanGoiMonDTO;
import com.quanlycafe.cafe_management.entity.Ban;
import com.quanlycafe.cafe_management.entity.ChiTietDatBan;
import com.quanlycafe.cafe_management.entity.HoaDon;
import com.quanlycafe.cafe_management.repository.BanRepository;
import com.quanlycafe.cafe_management.repository.ChiTietDatBanRepository;
import com.quanlycafe.cafe_management.repository.HoaDonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TablesService
 * Version 1.4
 * Date: 16-06-2026
 * Modification Logs:
 * DATE         AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026   lhthoai      Create
 * 09-06-2026   Quản Lý      Fix Booking Table Order Logic
 * 13-06-2026   Quản Lý      Gỡ bỏ logic kiểm tra và trừ tồn kho tự động
 * 16-06-2026   Quản Lý      Đồng bộ ORDER BY DESC cho 2 HĐ song song và gộp UI Thẻ bàn
 */
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

    public Page<Ban> getTablesWithPagination(String status, String search, Pageable pageable) {
        String keyword = (search == null) ? "" : search.trim();
        if (status == null || status.isEmpty() || status.equals("Tất cả")) {
            return banRepository.findByTenBanContainingIgnoreCase(keyword, pageable);
        }
        String tinhTrangDb = switch (status) {
            case "Có khách" -> "Đang sử dụng";
            case "Đã đặt" -> "Đã đặt trước";
            case "Trống" -> "Trống";
            default -> "";
        };
        return banRepository.findByTinhTrangAndTenBanContainingIgnoreCase(tinhTrangDb, keyword, pageable);
    }

    public long countTongSoBan() {
        return banRepository.count();
    }

    public long countBanByTinhTrang(String tinhTrang) {
        return banRepository.countByTinhTrang(tinhTrang);
    }

    public List<Ban> getBanByTinhTrang(String tinhTrang) {
        List<Ban> bans = banRepository.findByTinhTrang(tinhTrang);
        bans.sort((b1, b2) -> b1.getTenBan().compareToIgnoreCase(b2.getTenBan()));
        return bans;
    }

    /**
     * Lấy chi tiết thông tin gọi món và hóa đơn của một bàn (Luôn ưu tiên HĐ mới nhất)
     */
    public ThongTinBanGoiMonDTO getChiTietGoiMonTheoBan(Integer maBan) {
        Ban ban = banRepository.findById(maBan).orElse(null);
        if (ban == null) return null;

        ThongTinBanGoiMonDTO dto = new ThongTinBanGoiMonDTO();
        dto.setMaBan(ban.getMaBan());
        dto.setTenBan(ban.getTenBan());
        dto.setTinhTrang(ban.getTinhTrang());

        if (!"Đang sử dụng".equalsIgnoreCase(ban.getTinhTrang())) {
            dto.setDanhSachMon(List.of());
            dto.setTongTien(0.0);
            return dto;
        }

        // 🚀 Đã thêm ORDER BY hd.ngaygiotao DESC LIMIT 1 để lấy HĐ của Khách vãng lai lên trước
        String sqlHoaDon = "SELECT hd.mahoadon, hd.tongtien FROM hoadon hd " +
                "JOIN chitietdatban ctdb ON hd.mahoadon = ctdb.mahoadon " +
                "WHERE ctdb.maban = ? AND hd.trangthai = 'Chưa thanh toán' " +
                "ORDER BY hd.ngaygiotao DESC LIMIT 1";

        try {
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sqlHoaDon, maBan);
            if (!rows.isEmpty()) {
                Map<String, Object> row = rows.get(0);
                Integer maHoaDon = ((Number) row.get("mahoadon")).intValue();
                Double tongTien = ((Number) row.get("tongtien")).doubleValue();

                dto.setMaHoaDon(maHoaDon);
                dto.setTongTien(tongTien);

                String sqlChiTiet = "SELECT td.tenmon, cthd.soluong, cthd.giataithoidiemban, cthd.thanhtien " +
                        "FROM chitiethoadon cthd " +
                        "JOIN thucdon td ON cthd.mathucdon = td.mathucdon " +
                        "WHERE cthd.mahoadon = ?";

                List<ChiTietGoiMonDTO> danhSachMon = jdbcTemplate.query(sqlChiTiet, (rs, rowNum) -> {
                    ChiTietGoiMonDTO item = new ChiTietGoiMonDTO();
                    item.setTenMon(rs.getString("tenmon"));
                    item.setSoLuong(rs.getInt("soluong"));
                    item.setGiaTaiThoiDiemBan(rs.getDouble("giataithoidiemban"));
                    item.setThanhTien(rs.getDouble("thanhtien"));
                    return item;
                }, maHoaDon);

                dto.setDanhSachMon(danhSachMon);
            }
        } catch (Exception e) {
            dto.setDanhSachMon(List.of());
            dto.setTongTien(0.0);
        }
        return dto;
    }

    /**
     * Lấy danh sách món ăn đang phục vụ tại bàn dưới dạng Map JSON
     */
    public List<Map<String, Object>> getDanhSachMonJsonTheoBan(Integer maBan) {
        // 🚀 Dùng Sub-query bắt HĐ mới nhất
        String sql = "SELECT cthd.mathucdon, td.tenmon, cthd.soluong, cthd.giataithoidiemban " +
                "FROM chitiethoadon cthd " +
                "JOIN thucdon td ON cthd.mathucdon = td.mathucdon " +
                "WHERE cthd.mahoadon = (" +
                "   SELECT hd.mahoadon FROM hoadon hd " +
                "   JOIN chitietdatban ctdb ON hd.mahoadon = ctdb.mahoadon " +
                "   WHERE ctdb.maban = ? AND hd.trangthai = 'Chưa thanh toán' " +
                "   ORDER BY hd.ngaygiotao DESC LIMIT 1" +
                ")";
        return jdbcTemplate.queryForList(sql, maBan);
    }

    @Transactional
    public boolean chuyenBan(Integer tuMaBan, Integer denMaBan) {
        Ban tuBan = banRepository.findById(tuMaBan).orElse(null);
        Ban denBan = banRepository.findById(denMaBan).orElse(null);

        if (tuBan == null || denBan == null || !"Đang sử dụng".equalsIgnoreCase(tuBan.getTinhTrang()) || !"Trống".equalsIgnoreCase(denBan.getTinhTrang())) {
            return false;
        }

        // Chỉ chuyển hóa đơn đang hoạt động (mới nhất) đi
        String sqlLayHoaDon = "SELECT hd.mahoadon FROM hoadon hd JOIN chitietdatban ctdb ON hd.mahoadon = ctdb.mahoadon WHERE ctdb.maban = ? AND hd.trangthai = 'Chưa thanh toán' ORDER BY hd.ngaygiotao DESC LIMIT 1";
        try {
            Integer maHoaDon = jdbcTemplate.queryForObject(sqlLayHoaDon, Integer.class, tuMaBan);
            String sqlChuyen = "UPDATE chitietdatban SET maban = ? WHERE mahoadon = ?";
            jdbcTemplate.update(sqlChuyen, denMaBan, maHoaDon);

            // Kiểm tra xem bàn cũ còn Hóa đơn giữ chỗ không?
            String checkRemaining = "SELECT count(*) FROM hoadon hd JOIN chitietdatban ctdb ON hd.mahoadon = ctdb.mahoadon WHERE ctdb.maban = ? AND hd.trangthai = 'Chưa thanh toán'";
            Integer remainingCount = jdbcTemplate.queryForObject(checkRemaining, Integer.class, tuMaBan);
            if (remainingCount != null && remainingCount > 0) {
                tuBan.setTinhTrang("Đã đặt trước");
            } else {
                tuBan.setTinhTrang("Trống");
            }
            denBan.setTinhTrang("Đang sử dụng");
            banRepository.save(tuBan);
            banRepository.save(denBan);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean gopBan(List<Integer> tuMaBanList, Integer denMaBan) {
        try {
            String sqlLayHoaDon = "SELECT hd.mahoadon FROM hoadon hd JOIN chitietdatban ctdb ON hd.mahoadon = ctdb.mahoadon WHERE ctdb.maban = ? AND hd.trangthai = 'Chưa thanh toán' ORDER BY hd.ngaygiotao DESC LIMIT 1";
            Integer maHoaDonDich = jdbcTemplate.queryForObject(sqlLayHoaDon, Integer.class, denMaBan);
            if (maHoaDonDich == null) return false;

            for (Integer tuMaBan : tuMaBanList) {
                Integer maHoaDonNguon = jdbcTemplate.queryForObject(sqlLayHoaDon, Integer.class, tuMaBan);
                if (maHoaDonNguon != null) {
                    String sqlMergeChiTiet =
                            "INSERT INTO chitiethoadon (mathucdon, mahoadon, soluong, giataithoidiemban, thanhtien) " +
                                    "SELECT mathucdon, ?, soluong, giataithoidiemban, thanhtien FROM chitiethoadon WHERE mahoadon = ? " +
                                    "ON CONFLICT (mathucdon, mahoadon) DO UPDATE " +
                                    "SET soluong = chitiethoadon.soluong + EXCLUDED.soluong, " +
                                    "thanhtien = chitiethoadon.thanhtien + EXCLUDED.thanhtien";

                    jdbcTemplate.update(sqlMergeChiTiet, maHoaDonDich, maHoaDonNguon);
                    jdbcTemplate.update("DELETE FROM chitietdatban WHERE mahoadon = ?", maHoaDonNguon);
                    jdbcTemplate.update("DELETE FROM hoadon WHERE mahoadon = ?", maHoaDonNguon);
                }

                // Cập nhật tình trạng bàn gộp
                String checkRemaining = "SELECT count(*) FROM hoadon hd JOIN chitietdatban ctdb ON hd.mahoadon = ctdb.mahoadon WHERE ctdb.maban = ? AND hd.trangthai = 'Chưa thanh toán'";
                Integer remainingCount = jdbcTemplate.queryForObject(checkRemaining, Integer.class, tuMaBan);
                if (remainingCount != null && remainingCount > 0) {
                    jdbcTemplate.update("UPDATE ban SET tinhtrang = 'Đã đặt trước' WHERE maban = ?", tuMaBan);
                } else {
                    jdbcTemplate.update("UPDATE ban SET tinhtrang = 'Trống' WHERE maban = ?", tuMaBan);
                }
            }

            String sqlUpdateTongTien = "UPDATE hoadon SET tongtien = (SELECT COALESCE(SUM(thanhtien), 0) FROM chitiethoadon WHERE mahoadon = ?) WHERE mahoadon = ?";
            jdbcTemplate.update(sqlUpdateTongTien, maHoaDonDich, maHoaDonDich);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean tachBan(Integer tuMaBan, Integer denMaBan, List<Integer> mathucdonList, List<Integer> soluongTachList) {
        try {
            // Lấy HĐ mới nhất để tách
            String sqlLayThongTin = "SELECT ctdb.mahoadon, ctdb.manhanvien, ctdb.tenkhachhang, ctdb.sdtkhachhang, ctdb.ngaygiodat " +
                    "FROM chitietdatban ctdb JOIN hoadon hd ON ctdb.mahoadon = hd.mahoadon " +
                    "WHERE ctdb.maban = ? AND hd.trangthai = 'Chưa thanh toán' ORDER BY hd.ngaygiotao DESC LIMIT 1";
            Map<String, Object> thongTinCu = jdbcTemplate.queryForMap(sqlLayThongTin, tuMaBan);

            if (thongTinCu.isEmpty()) return false;

            Integer maHoaDonCu = (Integer) thongTinCu.get("mahoadon");
            Integer maNhanVien = (Integer) thongTinCu.get("manhanvien");
            String tenKhachHang = (String) thongTinCu.get("tenkhachhang");
            String sdtKhachHang = (String) thongTinCu.get("sdtkhachhang");
            Object ngayGioDat = thongTinCu.get("ngaygiodat");

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> connection.prepareStatement("INSERT INTO hoadon (tongtien, trangthai) VALUES (0, 'Chưa thanh toán')", new String[]{"mahoadon"}), keyHolder);

            Integer maHoaDonMoi = null;
            if (keyHolder.getKeys() != null && keyHolder.getKeys().get("mahoadon") != null) {
                maHoaDonMoi = ((Number) keyHolder.getKeys().get("mahoadon")).intValue();
            }
            if (maHoaDonMoi == null) return false;

            jdbcTemplate.update("INSERT INTO chitietdatban (maban, mahoadon, manhanvien, tenkhachhang, sdtkhachhang, ngaygiodat) VALUES (?, ?, ?, ?, ?, ?)", denMaBan, maHoaDonMoi, maNhanVien, tenKhachHang, sdtKhachHang, ngayGioDat);

            for (int i = 0; i < mathucdonList.size(); i++) {
                Integer maThucDon = mathucdonList.get(i);
                Integer slTach = soluongTachList.get(i);

                if (slTach == null || slTach <= 0) continue;

                Map<String, Object> cthdCu = jdbcTemplate.queryForMap("SELECT soluong, giataithoidiemban FROM chitiethoadon WHERE mahoadon = ? AND mathucdon = ?", maHoaDonCu, maThucDon);
                int slHienTai = (int) cthdCu.get("soluong");
                BigDecimal giaBan = (BigDecimal) cthdCu.get("giataithoidiemban");

                if (slTach > slHienTai) return false;

                double thanhTienMoi = slTach * giaBan.doubleValue();
                jdbcTemplate.update("INSERT INTO chitiethoadon (mathucdon, mahoadon, soluong, giataithoidiemban, thanhtien) VALUES (?, ?, ?, ?, ?)", maThucDon, maHoaDonMoi, slTach, giaBan, thanhTienMoi);

                int slConLai = slHienTai - slTach;
                if (slConLai == 0) {
                    jdbcTemplate.update("DELETE FROM chitiethoadon WHERE mahoadon = ? AND mathucdon = ?", maHoaDonCu, maThucDon);
                } else {
                    double thanhTienConLai = slConLai * giaBan.doubleValue();
                    jdbcTemplate.update("UPDATE chitiethoadon SET soluong = ?, thanhtien = ? WHERE mahoadon = ? AND mathucdon = ?", slConLai, thanhTienConLai, maHoaDonCu, maThucDon);
                }
            }

            String sqlUpdateTongTien = "UPDATE hoadon SET tongtien = (SELECT COALESCE(SUM(thanhtien), 0) FROM chitiethoadon WHERE mahoadon = ?) WHERE mahoadon = ?";
            jdbcTemplate.update(sqlUpdateTongTien, maHoaDonCu, maHoaDonCu);
            jdbcTemplate.update(sqlUpdateTongTien, maHoaDonMoi, maHoaDonMoi);

            jdbcTemplate.update("UPDATE ban SET tinhtrang = 'Đang sử dụng' WHERE maban = ?", denMaBan);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public void datBanTruoc(Integer maBan, Integer maNhanVien, String tenKhachHang, String sdtKhachHang, LocalDateTime ngayGioDat) {
        HoaDon hoaDonMoi = new HoaDon();
        hoaDonMoi.setTrangThai("Chưa thanh toán");
        hoaDonMoi.setTongTien(0.0);
        hoaDonMoi = hoaDonRepository.save(hoaDonMoi);

        ChiTietDatBan datBan = new ChiTietDatBan();
        datBan.setMaBan(maBan);
        datBan.setMaHoaDon(hoaDonMoi.getMaHoaDon());
        datBan.setTenKhachHang(tenKhachHang);
        datBan.setSdtKhachHang(sdtKhachHang);
        datBan.setNgayGioDat(ngayGioDat);
        datBan.setMaNhanVien(maNhanVien);

        chiTietDatBanRepository.save(datBan);
        Ban ban = banRepository.findById(maBan).orElseThrow(() -> new RuntimeException("Không tìm thấy bàn yêu cầu"));
        ban.setTinhTrang("Đã đặt trước");
        banRepository.save(ban);
    }

    @Transactional
    public void themMonVaoBan(Integer maBan, Integer maNhanVien, List<Integer> danhSachMaMon, List<Integer> danhSachSoLuong, String loaiKhach) {
        boolean coMonDuocGoi = false;
        if (danhSachMaMon != null && danhSachSoLuong != null) {
            for (Integer soLuong : danhSachSoLuong) {
                if (soLuong != null && soLuong > 0) {
                    coMonDuocGoi = true;
                    break;
                }
            }
        }
        if (!coMonDuocGoi) throw new RuntimeException("Vui lòng chọn ít nhất 1 món để thực hiện order!");

        Ban ban = banRepository.findById(maBan).orElseThrow(() -> new RuntimeException("Không tìm thấy bàn"));
        Integer maHoaDon = null;

        if ("Trống".equalsIgnoreCase(ban.getTinhTrang())) {
            ban.setTinhTrang("Đang sử dụng");
            banRepository.save(ban);
            String sqlInsertHoaDon = "INSERT INTO hoadon (tongtien, trangthai) VALUES (0, 'Chưa thanh toán') RETURNING mahoadon";
            maHoaDon = jdbcTemplate.queryForObject(sqlInsertHoaDon, Integer.class);
            String sqlInsertDatBan = "INSERT INTO chitietdatban (maban, manhanvien, mahoadon, tenkhachhang, ngaygiodat) VALUES (?, ?, ?, 'Khách vãng lai', CURRENT_TIMESTAMP)";
            jdbcTemplate.update(sqlInsertDatBan, maBan, maNhanVien, maHoaDon);

        } else if ("Đã đặt trước".equalsIgnoreCase(ban.getTinhTrang())) {
            ban.setTinhTrang("Đang sử dụng");
            banRepository.save(ban);

            if ("khachkhac".equals(loaiKhach)) {
                String sqlInsertHoaDon = "INSERT INTO hoadon (tongtien, trangthai) VALUES (0, 'Chưa thanh toán') RETURNING mahoadon";
                maHoaDon = jdbcTemplate.queryForObject(sqlInsertHoaDon, Integer.class);
                String sqlInsertDatBan = "INSERT INTO chitietdatban (maban, manhanvien, mahoadon, tenkhachhang, ngaygiodat) VALUES (?, ?, ?, 'Khách vãng lai', CURRENT_TIMESTAMP)";
                jdbcTemplate.update(sqlInsertDatBan, maBan, maNhanVien, maHoaDon);
            } else {
                String sqlFindHoaDon = "SELECT hd.mahoadon FROM hoadon hd JOIN chitietdatban ctdb ON hd.mahoadon = ctdb.mahoadon WHERE ctdb.maban = ? AND hd.trangthai = 'Chưa thanh toán' ORDER BY hd.ngaygiotao ASC LIMIT 1";
                maHoaDon = jdbcTemplate.queryForObject(sqlFindHoaDon, Integer.class, maBan);
            }
        } else {
            String sqlFindHoaDon = "SELECT hd.mahoadon FROM hoadon hd JOIN chitietdatban ctdb ON hd.mahoadon = ctdb.mahoadon WHERE ctdb.maban = ? AND hd.trangthai = 'Chưa thanh toán' ORDER BY hd.ngaygiotao DESC LIMIT 1";
            maHoaDon = jdbcTemplate.queryForObject(sqlFindHoaDon, Integer.class, maBan);
        }

        if (maHoaDon != null && danhSachMaMon != null) {
            for (int i = 0; i < danhSachMaMon.size(); i++) {
                Integer maMon = danhSachMaMon.get(i);
                Integer soLuong = danhSachSoLuong.get(i);

                String checkExist = "SELECT count(*) FROM chitiethoadon WHERE mahoadon = ? AND mathucdon = ?";
                Integer count = jdbcTemplate.queryForObject(checkExist, Integer.class, maHoaDon, maMon);

                if (soLuong != null && soLuong > 0) {
                    String sqlGiaMon = "SELECT giatienhientai FROM thucdon WHERE mathucdon = ?";
                    Double giaTien = jdbcTemplate.queryForObject(sqlGiaMon, Double.class, maMon);
                    Double thanhTien = giaTien * soLuong;

                    if (count != null && count > 0) {
                        String updateMon = "UPDATE chitiethoadon SET soluong = ?, thanhtien = ? WHERE mahoadon = ? AND mathucdon = ?";
                        jdbcTemplate.update(updateMon, soLuong, thanhTien, maHoaDon, maMon);
                    } else {
                        String insertMon = "INSERT INTO chitiethoadon (mathucdon, mahoadon, soluong, giataithoidiemban, thanhtien) VALUES (?, ?, ?, ?, ?)";
                        jdbcTemplate.update(insertMon, maMon, maHoaDon, soLuong, giaTien, thanhTien);
                    }
                } else if (soLuong != null && soLuong == 0) {
                    if (count != null && count > 0) {
                        String deleteMon = "DELETE FROM chitiethoadon WHERE mahoadon = ? AND mathucdon = ?";
                        jdbcTemplate.update(deleteMon, maHoaDon, maMon);
                    }
                }
            }
            String updateTongTien = "UPDATE hoadon SET tongtien = (SELECT COALESCE(SUM(thanhtien), 0) FROM chitiethoadon WHERE mahoadon = ?) WHERE mahoadon = ?";
            jdbcTemplate.update(updateTongTien, maHoaDon, maHoaDon);
        }
    }

    @Transactional
    public void thanhToanHoaDon(Integer maBan, Integer maKhuyenMai) {
        String sqlFindHoaDon = "SELECT hd.mahoadon, hd.tongtien FROM hoadon hd " +
                "JOIN chitietdatban ctdb ON hd.mahoadon = ctdb.mahoadon " +
                "WHERE ctdb.maban = ? AND hd.trangthai = 'Chưa thanh toán' ORDER BY hd.ngaygiotao DESC LIMIT 1";

        Map<String, Object> hoaDonMap;
        try {
            hoaDonMap = jdbcTemplate.queryForMap(sqlFindHoaDon, maBan);
        } catch (EmptyResultDataAccessException e) {
            throw new RuntimeException("Không tìm thấy hóa đơn chưa thanh toán cho bàn này!");
        }

        Integer maHoaDon = ((Number) hoaDonMap.get("mahoadon")).intValue();
        Double tongTienBanDau = ((Number) hoaDonMap.get("tongtien")).doubleValue();
        Double tongTienCuoiCung = tongTienBanDau;

        if (maKhuyenMai != null) {
            String sqlKm = "SELECT loaikhuyenmai, giatrigiam FROM khuyenmai WHERE makhuyenmai = ?";
            try {
                Map<String, Object> kmMap = jdbcTemplate.queryForMap(sqlKm, maKhuyenMai);
                String loaiKm = (String) kmMap.get("loaikhuyenmai");
                Double giaTriGiam = ((Number) kmMap.get("giatrigiam")).doubleValue();
                if (loaiKm != null && loaiKm.toLowerCase().contains("phần")) {
                    tongTienCuoiCung = tongTienBanDau - (tongTienBanDau * giaTriGiam / 100);
                } else {
                    tongTienCuoiCung = tongTienBanDau - giaTriGiam;
                }
                if (tongTienCuoiCung < 0) tongTienCuoiCung = 0.0;
            } catch (Exception e) {
                maKhuyenMai = null;
            }
        }

        String sqlUpdateHoaDon = "UPDATE hoadon SET trangthai = 'Đã thanh toán', tongtien = ?, makhuyenmai = ? WHERE mahoadon = ?";
        jdbcTemplate.update(sqlUpdateHoaDon, tongTienCuoiCung, maKhuyenMai, maHoaDon);

        // 🚀 CẬP NHẬT: Trả bàn về đúng trạng thái
        String checkRemaining = "SELECT count(*) FROM hoadon hd JOIN chitietdatban ctdb ON hd.mahoadon = ctdb.mahoadon WHERE ctdb.maban = ? AND hd.trangthai = 'Chưa thanh toán'";
        Integer remainingCount = jdbcTemplate.queryForObject(checkRemaining, Integer.class, maBan);

        if (remainingCount != null && remainingCount > 0) {
            jdbcTemplate.update("UPDATE ban SET tinhtrang = 'Đã đặt trước' WHERE maban = ?", maBan);
        } else {
            jdbcTemplate.update("UPDATE ban SET tinhtrang = 'Trống' WHERE maban = ?", maBan);
        }
    }

    public List<Map<String, Object>> getKhuyenMaiHopLe() {
        String sql = "SELECT makhuyenmai, tenkhuyenmai, loaikhuyenmai, giatrigiam " +
                "FROM khuyenmai " +
                "WHERE flag_delete = 0 AND CURRENT_DATE >= ngaybatdau AND CURRENT_DATE <= ngayketthuc";
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> getDanhSachThucDonVoiTrangThai() {
        String sql = "SELECT td.mathucdon, td.tenmon, td.giatienhientai, " +
                "true AS is_available " +
                "FROM thucdon td " +
                "WHERE td.flag_delete = 0 " +
                "ORDER BY LOWER(td.tenmon) ASC";

        return jdbcTemplate.queryForList(sql);
    }

    @Transactional
    public void huyBan(Integer maBan) {
        Ban ban = banRepository.findById(maBan)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn yêu cầu"));

        if ("Trống".equalsIgnoreCase(ban.getTinhTrang())) {
            throw new RuntimeException("Bàn đang trống, không thể hủy!");
        }

        String sqlFindHoaDon = "SELECT hd.mahoadon FROM hoadon hd " +
                "JOIN chitietdatban ctdb ON hd.mahoadon = ctdb.mahoadon " +
                "WHERE ctdb.maban = ? AND hd.trangthai = 'Chưa thanh toán' ORDER BY hd.ngaygiotao DESC LIMIT 1";
        Integer maHoaDon = null;
        try {
            maHoaDon = jdbcTemplate.queryForObject(sqlFindHoaDon, Integer.class, maBan);
        } catch (EmptyResultDataAccessException e) {
        }

        if (maHoaDon != null) {
            String sqlCancelHoaDon = "UPDATE hoadon SET trangthai = 'Đã hủy', flag_delete = 1 WHERE mahoadon = ?";
            jdbcTemplate.update(sqlCancelHoaDon, maHoaDon);
        }

        // Cập nhật lại trạng thái bàn sau khi xóa
        String checkRemaining = "SELECT count(*) FROM hoadon hd JOIN chitietdatban ctdb ON hd.mahoadon = ctdb.mahoadon WHERE ctdb.maban = ? AND hd.trangthai = 'Chưa thanh toán'";
        Integer remainingCount = jdbcTemplate.queryForObject(checkRemaining, Integer.class, maBan);

        if (remainingCount != null && remainingCount > 0) {
            ban.setTinhTrang("Đã đặt trước");
        } else {
            ban.setTinhTrang("Trống");
        }
        banRepository.save(ban);
    }

    /**
     * 🚀 Truy xuất thông tin đặt bàn & Khách đang sử dụng (Gộp hiển thị UI)
     */
    public Map<Integer, String> getThongTinDatBanMap() {
        Map<Integer, String> map = new HashMap<>();

        // Sắp xếp giảm dần để HĐ Khách vãng lai (mới nhất) luôn đè lên HĐ Đặt bàn
        String sql = "SELECT ctdb.maban, ctdb.tenkhachhang, ctdb.sdtkhachhang, ctdb.ngaygiodat, hd.mahoadon " +
                "FROM chitietdatban ctdb " +
                "JOIN hoadon hd ON ctdb.mahoadon = hd.mahoadon " +
                "WHERE hd.trangthai = 'Chưa thanh toán' ORDER BY hd.ngaygiotao DESC";

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM");

        Map<Integer, StringBuilder> htmlMap = new HashMap<>();
        Map<Integer, Integer> primaryInvoiceMap = new HashMap<>();

        for (Map<String, Object> row : rows) {
            Integer maBan = ((Number) row.get("maban")).intValue();
            String ten = (String) row.get("tenkhachhang");
            String sdt = (String) row.get("sdtkhachhang");
            Object dateObj = row.get("ngaygiodat");
            Integer maHoaDon = ((Number) row.get("mahoadon")).intValue();

            String timeStr = "";
            if (dateObj instanceof java.sql.Timestamp) {
                timeStr = ((java.sql.Timestamp) dateObj).toLocalDateTime().format(formatter);
            } else if (dateObj instanceof LocalDateTime) {
                timeStr = ((LocalDateTime) dateObj).format(formatter);
            }

            String phoneDisplay = (sdt != null && !sdt.isEmpty()) ? sdt : "Chưa có SĐT";

            String infoHtml = "";
            if (ten != null && !ten.equals("Khách vãng lai")) {
                infoHtml = "<div class='mb-1'><span class='text-blue-600 font-extrabold'><i class='fa-solid fa-address-book mr-1'></i>" + ten + "</span><br>" +
                        "<span class='font-semibold text-gray-500 text-[9px]'>HĐ: #" + maHoaDon + " • " + phoneDisplay + " • " + timeStr + "</span></div>";
            } else {
                infoHtml = "<div class='mb-1'><span class='text-[#553722] font-extrabold'><i class='fa-solid fa-user mr-1'></i>Khách tại quán</span><br>" +
                        "<span class='font-semibold text-gray-500 text-[9px]'>HĐ: #" + maHoaDon + " • " + timeStr + "</span></div>";
            }

            // Nếu bàn chưa có trong map (Tức là đây là Hóa đơn mới nhất của bàn đó)
            if (!htmlMap.containsKey(maBan)) {
                htmlMap.put(maBan, new StringBuilder(infoHtml));
                primaryInvoiceMap.put(maBan, maHoaDon); // Đánh dấu đây là Hóa đơn chính để Javascript bắt sự kiện
            } else {
                // Nếu bàn đã có (Tức là đang có 2 HĐ song song), nhét thông tin Đặt bàn xuống dưới, phân cách bởi viền mờ
                htmlMap.get(maBan).append("<div class='border-t border-dashed border-gray-300 my-1'></div>").append(infoHtml);
            }
        }

        // Bọc input hidden cho Javascript
        for (Map.Entry<Integer, StringBuilder> entry : htmlMap.entrySet()) {
            String finalHtml = entry.getValue().toString() + "<input type='hidden' class='hidden-invoice-id' value='" + primaryInvoiceMap.get(entry.getKey()) + "'>";
            map.put(entry.getKey(), finalHtml);
        }

        return map;
    }
}