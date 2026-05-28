package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.UserProfileDTO;
import com.quanlycafe.cafe_management.entity.Ban;
import com.quanlycafe.cafe_management.entity.ThucDon;
import com.quanlycafe.cafe_management.repository.ThucDonRepository;
import com.quanlycafe.cafe_management.service.TablesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.quanlycafe.cafe_management.dto.ThongTinBanGoiMonDTO;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Controller
public class TablesController {

    @Autowired
    private TablesService tablesService;

    @Autowired
    private ThucDonRepository thucDonRepository;

    @GetMapping("/tables")
    public String showTableMap(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "search", required = false) String search,
            Model model) {

        List<Ban> tatCaBan = tablesService.getAllTables();
        List<ThucDon> danhSachThucDon = thucDonRepository.findAll();

        // 1. Đếm số lượng tổng
        long tongSoBan = tatCaBan.size();
        long soBanTrong = tatCaBan.stream().filter(b -> "Trống".equalsIgnoreCase(b.getTinhTrang())).count();
        long soBanCoKhach = tatCaBan.stream().filter(b -> "Đang sử dụng".equalsIgnoreCase(b.getTinhTrang())).count();
        long soBanDaDat = tatCaBan.stream().filter(b -> "Đã đặt trước".equalsIgnoreCase(b.getTinhTrang())).count();

        // 2. Tìm kiếm và lọc
        List<Ban> danhSachHienThi = tatCaBan.stream().filter(b -> {
            // Lọc theo trạng thái
            boolean matchesStatus = (status == null || status.isEmpty() || status.equals("Tất cả"))
                    || (status.equals("Có khách") && "Đang sử dụng".equalsIgnoreCase(b.getTinhTrang()))
                    || (status.equals("Đã đặt") && "Đã đặt trước".equalsIgnoreCase(b.getTinhTrang()))
                    || (status.equals("Trống") && "Trống".equalsIgnoreCase(b.getTinhTrang()));

            // Lọc theo từ khóa tìm kiếm
            boolean matchesSearch = (search == null || search.trim().isEmpty())
                    || b.getTenBan().toLowerCase().contains(search.trim().toLowerCase());

            return matchesStatus && matchesSearch;
        }).toList();

        // 3. Trả dữ liệu
        model.addAttribute("danhSachBan", danhSachHienThi);
        model.addAttribute("tongSoBan", tongSoBan);
        model.addAttribute("soBanTrong", soBanTrong);
        model.addAttribute("soBanCoKhach", soBanCoKhach);
        model.addAttribute("soBanDaDat", soBanDaDat);
        model.addAttribute("currentStatus", status != null ? status : "Tất cả");
        model.addAttribute("currentSearch", search != null ? search : "");
        model.addAttribute("danhSachBanTrong", tatCaBan.stream().filter(b -> "Trống".equalsIgnoreCase(b.getTinhTrang())).toList());
        model.addAttribute("danhSachBanCoKhach", tatCaBan.stream().filter(b -> "Đang sử dụng".equalsIgnoreCase(b.getTinhTrang())).toList());
        model.addAttribute("danhSachThucDon", danhSachThucDon);
        return "tables";
    }

    @GetMapping("/tables/{maBan}/order-details")
    public String getOrderDetailsFragment(@PathVariable("maBan") Integer maBan, Model model) {
        ThongTinBanGoiMonDTO thongTinGoiMon = tablesService.getChiTietGoiMonTheoBan(maBan);
        model.addAttribute("order", thongTinGoiMon);

        return "fragments/hoadon :: nội_dung_hóa_đơn";
    }

    @PostMapping("/tables/transfer")
    public String transferTable(@RequestParam("tuMaBan") Integer tuMaBan,
                                @RequestParam("denMaBan") Integer denMaBan) {
        tablesService.chuyenBan(tuMaBan, denMaBan);
        return "redirect:/tables";
    }

    @PostMapping("/tables/merge")
    public String mergeTables(@RequestParam(value = "tuMaBanList", required = false) List<Integer> tuMaBanList,
                              @RequestParam(value = "denMaBan", required = true) Integer denMaBan,
                              org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        if (tuMaBanList == null || tuMaBanList.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Vui lòng chọn ít nhất 1 bàn để gộp!");
            return "redirect:/tables";
        }

        // Loại bỏ bàn đích khỏi danh sách nguồn
        tuMaBanList.remove(denMaBan);

        if (tuMaBanList.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Bàn cần gộp không hợp lệ!");
            return "redirect:/tables";
        }

        boolean success = tablesService.gopBan(tuMaBanList, denMaBan);
        if (success) {
            redirectAttributes.addFlashAttribute("successMsg", "Gộp bàn thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "Có lỗi xảy ra khi gộp bàn!");
        }
        return "redirect:/tables";
    }

    @GetMapping("/tables/{maBan}/items")
    @ResponseBody
    public List<Map<String, Object>> getTableItemsJson(@PathVariable("maBan") Integer maBan) {
        return tablesService.getDanhSachMonJsonTheoBan(maBan);
    }

    @PostMapping("/tables/split")
    public String splitTable(@RequestParam("tuMaBan") Integer tuMaBan,
                             @RequestParam("denMaBan") Integer denMaBan,
                             @RequestParam("mathucdonList") List<Integer> mathucdonList,
                             @RequestParam("soluongTachList") List<Integer> soluongTachList,
                             org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        boolean success = tablesService.tachBan(tuMaBan, denMaBan, mathucdonList, soluongTachList);
        if (success) {
            redirectAttributes.addFlashAttribute("successMsg", "Tách hóa đơn thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "Tách hóa đơn thất bại hoặc số lượng không hợp lệ!");
        }
        return "redirect:/tables";
    }

    @PostMapping("/tables/booking")
    public String handleBookingTable(
            @ModelAttribute("currentUser") UserProfileDTO currentUser, // <-- Lấy trực tiếp từ GlobalControllerAdvice
            @RequestParam("maBan") Integer maBan,
            @RequestParam("tenKhachHang") String tenKhachHang,
            @RequestParam(value = "sdtKhachHang", required = false) String sdtKhachHang,
            @RequestParam("ngayDat") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayDat,
            @RequestParam("gioDat") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime gioDat,
            RedirectAttributes redirectAttributes) {

        try {
            // Kiểm tra an toàn phòng trường hợp mất session
            if (currentUser == null || currentUser.getMaNhanVien() == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Phiên đăng nhập đã hết hạn, vui lòng đăng nhập lại!");
                return "redirect:/login";
            }

            LocalDateTime ngayGioDat = LocalDateTime.of(ngayDat, gioDat);

            Ban ban = tablesService.findById(maBan);
            if (ban == null || !ban.getTinhTrang().equals("Trống")) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bàn này không còn trống để đặt!");
                return "redirect:/tables";
            }

            // TRUYỀN MÃ NHÂN VIÊN THỰC TẾ VÀO SERVICE
            tablesService.datBanTruoc(maBan, currentUser.getMaNhanVien(), tenKhachHang, sdtKhachHang, ngayGioDat);

            redirectAttributes.addFlashAttribute("successMessage", "Đã đặt bàn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi đặt bàn: " + e.getMessage());
        }

        return "redirect:/tables";
    }

    @PostMapping("/tables/order")
    public String xuLyGoiMon(
            @ModelAttribute("currentUser") UserProfileDTO currentUser,
            @RequestParam("maBan") Integer maBan,
            @RequestParam(value = "maThucDon", required = false) List<Integer> danhSachMaMon,
            @RequestParam(value = "soLuong", required = false) List<Integer> danhSachSoLuong,
            RedirectAttributes redirectAttributes) {
        try {
            Integer maNhanVien = currentUser.getMaNhanVien();

            if (danhSachMaMon != null && !danhSachMaMon.isEmpty()) {
                tablesService.themMonVaoBan(maBan, maNhanVien, danhSachMaMon, danhSachSoLuong);
                redirectAttributes.addFlashAttribute("successMessage", "Đã thêm món thành công!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi gọi món: " + e.getMessage());
        }
        return "redirect:/tables";
    }

    @PostMapping("/tables/checkout")
    public String xuLyThanhToan(@RequestParam("maBan") Integer maBan, RedirectAttributes redirectAttributes) {
        try {
            tablesService.thanhToanHoaDon(maBan);
            redirectAttributes.addFlashAttribute("successMessage", "Thanh toán thành công! Bàn đã được dọn trống.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi thanh toán: " + e.getMessage());
        }
        return "redirect:/tables";
    }
}