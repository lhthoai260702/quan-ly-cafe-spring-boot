package com.quanlycafe.cafe_management.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * MenuFormDTO
 * <p>
 * Version 1.1
 * <p>
 * Date: 30-05-2026
 * <p>
 * Copyright
 * <p>
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 30-05-2026 lhthoai      Create DTO cho form Thêm/Sửa thực đơn
 * 07-06-2026 Quản Lý      Add IngredientDTO list for ChiTietThucDon
 */
@Data
public class MenuFormDTO {
    private Integer maThucDon;

    @NotBlank(message = "Tên món không được để trống")
    @Size(max = 100, message = "Tên món không được vượt quá 100 ký tự")
    private String tenMon;

    @NotBlank(message = "Vui lòng chọn phân loại món")
    @Size(max = 50, message = "Loại món không được vượt quá 50 ký tự")
    private String loaiMon;

    @NotNull(message = "Giá tiền không được để trống")
    @Min(value = 0, message = "Giá tiền không được là số âm")
    @Max(value = 99999999, message = "Giá tiền không được vượt quá 99.999.999 VNĐ")
    private Double giaTienHienTai;

    // Danh sách nguyên liệu (Chi tiết thực đơn)
    private List<IngredientDTO> ingredients = new ArrayList<>();

    @Data
    public static class IngredientDTO {
        private Integer maHangHoa;
        private Double khoiLuong;
    }
}