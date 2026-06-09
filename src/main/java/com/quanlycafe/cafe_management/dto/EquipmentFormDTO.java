package com.quanlycafe.cafe_management.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * EquipmentFormDTO
 * Version 1.2
 * Date: 09-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 30-05-2026   lhthoai     Create DTO cho form Thêm/Sửa thiết bị
 * 07-06-2026   lhthoai     Update DTO match new DB (remove soLuong, add tinhTrang)
 * 09-06-2026   lhthoai     Apply Java Coding Convention
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentFormDTO {

    private Integer maThietBi;

    @NotBlank(message = "Tên thiết bị không được để trống")
    @Size(max = 100, message = "Tên thiết bị không được vượt quá 100 ký tự")
    private String tenThietBi;

    @NotBlank(message = "Tình trạng không được để trống")
    private String tinhTrang = "Hoạt động tốt";

    private String ghiChu;

    @NotNull(message = "Ngày mua không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayMua;

    @Min(value = 0, message = "Đơn giá mua không được nhỏ hơn 0 VNĐ")
    private Double donGiaMua;

}