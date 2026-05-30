package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.ThongTinBanGoiMonDTO;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

/**
 * TablesController
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 * 30-05-2026 lthoai      Add Pagination
 */
@Controller
public class TablesController {

    @Autowired
    private TablesService tablesService;

    @Autowired
    private ThucDonRepository thucDonRepository;

    /**
     * Hiển thị danh sách bàn (Có phân trang)
     *
     * @param status String
     * @param search String
     * @param page   int
     * @param size   int
     * @param model  Model
     * @return String
     */
    @GetMapping("/tables")
    public String showTableMap(
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "16") int size,
            Model model) {

        // 1. Xử lý phân trang (Sắp xếp theo tên bàn mặc định)
        org.springframework.data.domain.Pageable pageable =
                org.springframework.data.domain.PageRequest.of(page - 1, size, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "tenBan"));

        org.springframework.data.domain.Page<Ban> banPage = tablesService.getTablesWithPagination(status, search, pageable);
        List<ThucDon> danhSachThucDon = thucDonRepository.findAll();

        // 2. Lấy số lượng thống kê (Tối ưu hóa: Dùng Count SQL thay vì tải toàn bộ DB lên RAM)
        model.addAttribute("tongSoBan", tablesService.countTongSoBan());
        model.addAttribute("soBanTrong", tablesService.countBanByTinhTrang("Trống"));
        model.addAttribute("soBanCoKhach", tablesService.countBanByTinhTrang("Đang sử dụng"));
        model.addAttribute("soBanDaDat", tablesService.countBanByTinhTrang("Đã đặt trước"));

        // 3. Trả dữ liệu hiển thị (Current Page)
        model.addAttribute("danhSachBan", banPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", banPage.getTotalPages());

        model.addAttribute("currentStatus", status != null ? status : "Tất cả");
        model.addAttribute("currentSearch", search != null ? search : "");

        // 4. Lấy Toàn bộ dữ liệu bàn để phục vụ Modal Gộp/Chuyển/Tách (Bắt buộc không phân trang)
        model.addAttribute("danhSachBanTrong", tablesService.getBanByTinhTrang("Trống"));
        model.addAttribute("danhSachBanCoKhach", tablesService.getBanByTinhTrang("Đang sử dụng"));
        model.addAttribute("danhSachThucDon", danhSachThucDon);

        return "tables";
    }

    /**
     * Hiển thị chi tiết order của bàn (danh sách món)
     *
     * @param maBan Integer
     * @param model Model
     * @return String
     */
    @GetMapping("/tables/{maBan}/order-details")
    public String getOrderDetailsFragment(@PathVariable("maBan") Integer maBan, Model model) {
        ThongTinBanGoiMonDTO thongTinGoiMon = tablesService.getChiTietGoiMonTheoBan(maBan);
        model.addAttribute("order", thongTinGoiMon);

        return "fragments/hoadon :: nội_dung_hóa_đơn";
    }

    /**
     * Chuyển bàn
     *
     * @param tuMaBan  Integer
     * @param denMaBan Integer
     * @return String
     */
    @PostMapping("/tables/transfer")
    public String transferTable(@RequestParam("tuMaBan") Integer tuMaBan,
                                @RequestParam("denMaBan") Integer denMaBan) {
        tablesService.chuyenBan(tuMaBan, denMaBan);
        return "redirect:/tables";
    }

    /**
     * Gộp bàn
     *
     * @param tuMaBanList        List<Integer>
     * @param denMaBan           Integer
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/tables/merge")
    public String mergeTables(@RequestParam(value = "tuMaBanList", required = false) List<Integer> tuMaBanList,
                              @RequestParam(value = "denMaBan", required = true) Integer denMaBan,
                              RedirectAttributes redirectAttributes) {

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

    /**
     * Lấy danh sách món của bàn (định dạng JSON dùng cho tách bàn)
     *
     * @param maBan Integer
     * @return List<Map<String, Object>>
     */
    @GetMapping("/tables/{maBan}/items")
    @ResponseBody
    public List<Map<String, Object>> getTableItemsJson(@PathVariable("maBan") Integer maBan) {
        return tablesService.getDanhSachMonJsonTheoBan(maBan);
    }

    /**
     * Tách bàn (tách hóa đơn)
     *
     * @param tuMaBan            Integer
     * @param denMaBan           Integer
     * @param mathucdonList      List<Integer>
     * @param soluongTachList    List<Integer>
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/tables/split")
    public String splitTable(@RequestParam("tuMaBan") Integer tuMaBan,
                             @RequestParam("denMaBan") Integer denMaBan,
                             @RequestParam("mathucdonList") List<Integer> mathucdonList,
                             @RequestParam("soluongTachList") List<Integer> soluongTachList,
                             RedirectAttributes redirectAttributes) {

        boolean success = tablesService.tachBan(tuMaBan, denMaBan, mathucdonList, soluongTachList);

        if (success) {
            redirectAttributes.addFlashAttribute("successMsg", "Tách hóa đơn thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "Tách hóa đơn thất bại hoặc số lượng không hợp lệ!");
        }

        return "redirect:/tables";
    }

    /**
     * Đặt bàn trước
     *
     * @param currentUser        UserProfileDTO
     * @param maBan              Integer
     * @param tenKhachHang       String
     * @param sdtKhachHang       String
     * @param ngayDat            LocalDate
     * @param gioDat             LocalTime
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
    @PostMapping("/tables/booking")
    public String handleBookingTable(
            @ModelAttribute("currentUser") UserProfileDTO currentUser,
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

            // Truyền mã nhân viên thực tế vào service
            tablesService.datBanTruoc(maBan, currentUser.getMaNhanVien(), tenKhachHang, sdtKhachHang, ngayGioDat);

            redirectAttributes.addFlashAttribute("successMessage", "Đã đặt bàn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi đặt bàn: " + e.getMessage());
        }

        return "redirect:/tables";
    }

    /**
     * Xử lý gọi món
     *
     * @param currentUser        UserProfileDTO
     * @param maBan              Integer
     * @param danhSachMaMon      List<Integer>
     * @param danhSachSoLuong    List<Integer>
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
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

    /**
     * Xử lý thanh toán hóa đơn
     *
     * @param maBan              Integer
     * @param redirectAttributes RedirectAttributes
     * @return String
     */
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