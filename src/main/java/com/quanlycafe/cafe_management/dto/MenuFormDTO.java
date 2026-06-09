package com.quanlycafe.cafe_management.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * MenuFormDTO
 * Version 1.2
 * Date: 09-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 30-05-2026   lhthoai     Create DTO cho form Thêm/Sửa thực đơn
 * 07-06-2026   lhthoai     Add IngredientDTO list for ChiTietThucDon
 * 09-06-2026   lhthoai     Apply Java Coding Convention
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    private List<IngredientDTO> ingredients = new ArrayList<>();

    /**
     * IngredientDTO
     * DTO đại diện cho nguyên liệu trong thực đơn
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IngredientDTO {
        private Integer maHangHoa;
        private Double khoiLuong;
    }

}