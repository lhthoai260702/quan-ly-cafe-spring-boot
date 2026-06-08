package com.quanlycafe.cafe_management.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseFormDTO {
    private Integer id; // Phục vụ chức năng Edit

    @NotBlank(message = "Vui lòng nhập tên khoản chi")
    private String tenKhoanChi;

    @NotNull(message = "Vui lòng nhập số tiền")
    private Double soTien;

    @NotNull(message = "Vui lòng chọn ngày chi")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate ngayChi;
}