package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.ThongTinBanGoiMonDTO;
import com.quanlycafe.cafe_management.dto.UserProfileDTO;
import com.quanlycafe.cafe_management.entity.Ban;
import com.quanlycafe.cafe_management.repository.ThucDonRepository;
import com.quanlycafe.cafe_management.service.TablesService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
 * <p>
 * Version 1.1
 * <p>
 * Date: 09-06-2026
 * <p>
 * Copyright
 * <p>
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 29-05-2026   lhthoai     Create
 * 30-05-2026   lhthoai     Add Pagination
 * 09-06-2026   Quản Lý     Apply Java Coding Convention & Referer Redirection
 */
@Controller
@RequiredArgsConstructor
public class TablesController {

    private final TablesService tablesService;
    private final ThucDonRepository thucDonRepository;

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
            @RequestParam(defaultValue = "15") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page - 1, size,
                Sort.by(Sort.Order.asc("tenBan").ignoreCase()));

        Page<Ban> banPage = tablesService.getTablesWithPagination(status, search, pageable);
        List<Map<String, Object>> danhSachThucDon = tablesService.getDanhSachThucDonVoiTrangThai();

        model.addAttribute("tongSoBan", tablesService.countTongSoBan());
        model.addAttribute("soBanTrong", tablesService.countBanByTinhTrang("Trống"));
        model.addAttribute("soBanCoKhach", tablesService.countBanByTinhTrang("Đang sử dụng"));
        model.addAttribute("soBanDaDat", tablesService.countBanByTinhTrang("Đã đặt trước"));

        model.addAttribute("danhSachBan", banPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", banPage.getTotalPages());
        model.addAttribute("totalElements", banPage.getTotalElements());

        model.addAttribute("currentStatus", status != null ? status : "Tất cả");
        model.addAttribute("currentSearch", search != null ? search : "");

        model.addAttribute("danhSachBanTrong", tablesService.getBanByTinhTrang("Trống"));
        model.addAttribute("danhSachBanCoKhach", tablesService.getBanByTinhTrang("Đang sử dụng"));
        model.addAttribute("danhSachThucDon", danhSachThucDon);

        return "tables";
    }

    /**
     * Hiển thị chi tiết order của bàn (Fragment)
     *
     * @param maBan Integer
     * @param model Model
     * @return String
     */
    @GetMapping("/tables/{maBan}/order-details")
    public String getOrderDetailsFragment(@PathVariable("maBan") Integer maBan, Model model) {
        ThongTinBanGoiMonDTO thongTinGoiMon = tablesService.getChiTietGoiMonTheoBan(maBan);
        model.addAttribute("order", thongTinGoiMon);
        model.addAttribute("danhSachKhuyenMai", tablesService.getKhuyenMaiHopLe());

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
     * @param request            HttpServletRequest
     * @return String
     */
    @PostMapping("/tables/merge")
    public String mergeTables(@RequestParam(value = "tuMaBanList", required = false) List<Integer> tuMaBanList,
                              @RequestParam(value = "denMaBan") Integer denMaBan,
                              RedirectAttributes redirectAttributes,
                              HttpServletRequest request) {

        String referer = request.getHeader("Referer");
        String redirectUrl = referer != null ? referer : "/tables";

        if (tuMaBanList == null || tuMaBanList.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Vui lòng chọn ít nhất 1 bàn để gộp!");
            return "redirect:" + redirectUrl;
        }

        tuMaBanList.remove(denMaBan);
        if (tuMaBanList.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMsg", "Bàn cần gộp không hợp lệ!");
            return "redirect:" + redirectUrl;
        }

        boolean success = tablesService.gopBan(tuMaBanList, denMaBan);
        if (success) {
            redirectAttributes.addFlashAttribute("successMsg", "Gộp bàn thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "Có lỗi xảy ra khi gộp bàn!");
        }

        return "redirect:" + redirectUrl;
    }

    /**
     * Lấy danh sách món theo bàn (JSON)
     *
     * @param maBan Integer
     * @return List
     */
    @GetMapping("/tables/{maBan}/items")
    @ResponseBody
    public List<Map<String, Object>> getTableItemsJson(@PathVariable("maBan") Integer maBan) {
        return tablesService.getDanhSachMonJsonTheoBan(maBan);
    }

    /**
     * Tách hóa đơn bàn
     *
     * @param tuMaBan            Integer
     * @param denMaBan           Integer
     * @param mathucdonList      List<Integer>
     * @param soluongTachList    List<Integer>
     * @param redirectAttributes RedirectAttributes
     * @param request            HttpServletRequest
     * @return String
     */
    @PostMapping("/tables/split")
    public String splitTable(@RequestParam("tuMaBan") Integer tuMaBan,
                             @RequestParam("denMaBan") Integer denMaBan,
                             @RequestParam("mathucdonList") List<Integer> mathucdonList,
                             @RequestParam("soluongTachList") List<Integer> soluongTachList,
                             RedirectAttributes redirectAttributes,
                             HttpServletRequest request) {

        String referer = request.getHeader("Referer");
        String redirectUrl = referer != null ? referer : "/tables";

        boolean success = tablesService.tachBan(tuMaBan, denMaBan, mathucdonList, soluongTachList);
        if (success) {
            redirectAttributes.addFlashAttribute("successMsg", "Tách hóa đơn thành công!");
        } else {
            redirectAttributes.addFlashAttribute("errorMsg", "Tách hóa đơn thất bại!");
        }

        return "redirect:" + redirectUrl;
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
            if (currentUser == null || currentUser.getMaNhanVien() == null) {
                return "redirect:/login";
            }

            LocalDateTime ngayGioDat = LocalDateTime.of(ngayDat, gioDat);
            Ban ban = tablesService.findById(maBan);

            if (ban == null || !"Trống".equals(ban.getTinhTrang())) {
                redirectAttributes.addFlashAttribute("errorMessage", "Bàn không sẵn sàng để đặt!");
                return "redirect:/tables";
            }

            tablesService.datBanTruoc(maBan, currentUser.getMaNhanVien(), tenKhachHang, sdtKhachHang, ngayGioDat);
            redirectAttributes.addFlashAttribute("successMessage", "Đặt bàn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
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
     * @param request            HttpServletRequest
     * @return String
     */
    @PostMapping("/tables/order")
    public String xuLyGoiMon(
            @ModelAttribute("currentUser") UserProfileDTO currentUser,
            @RequestParam("maBan") Integer maBan,
            @RequestParam(value = "maThucDon", required = false) List<Integer> danhSachMaMon,
            @RequestParam(value = "soLuong", required = false) List<Integer> danhSachSoLuong,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {

        String referer = request.getHeader("Referer");
        String redirectUrl = referer != null ? referer : "/tables";

        try {
            if (danhSachMaMon != null && !danhSachMaMon.isEmpty()) {
                tablesService.themMonVaoBan(maBan, currentUser.getMaNhanVien(), danhSachMaMon, danhSachSoLuong);
                redirectAttributes.addFlashAttribute("successMsg", "Đã thêm món!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "Lỗi: " + e.getMessage());
        }

        return "redirect:" + redirectUrl;
    }

    /**
     * Xử lý thanh toán
     *
     * @param maBan              Integer
     * @param maKhuyenMai        Integer
     * @param redirectAttributes RedirectAttributes
     * @param request            HttpServletRequest
     * @return String
     */
    @PostMapping("/tables/checkout")
    public String xuLyThanhToan(@RequestParam("maBan") Integer maBan,
                                @RequestParam(value = "maKhuyenMai", required = false) Integer maKhuyenMai,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request) {

        String referer = request.getHeader("Referer");
        String redirectUrl = referer != null ? referer : "/tables";

        try {
            tablesService.thanhToanHoaDon(maBan, maKhuyenMai);
            redirectAttributes.addFlashAttribute("successMessage", "Thanh toán thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi thanh toán: " + e.getMessage());
        }

        return "redirect:" + redirectUrl;
    }
}