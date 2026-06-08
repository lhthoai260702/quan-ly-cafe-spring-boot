package com.quanlycafe.cafe_management.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * PhoneNumberValidator
 * * Version 1.1
 * * Date: 07-06-2026
 * * Copyright
 * * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 * 07-06-2026 lhthoai      Cập nhật chuẩn đầu vào: Bắt đầu bằng 0, có đúng 10 số
 */
public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
    }

    /**
     * validate phone number format
     *
     * @param phoneField
     * @param context
     * @return boolean
     */
    @Override
    public boolean isValid(String phoneField, ConstraintValidatorContext context) {
        if (phoneField == null || phoneField.trim().isEmpty()) {
            return false;
        }

        // Loại bỏ khoảng trắng hoặc gạch ngang (nếu Frontend truyền lên dạng đã format)
        String cleanPhone = phoneField.replaceAll("[\\s\\-]", "");

        // Biểu thức chính quy: Bắt đầu bằng 0, theo sau là 9 chữ số (tổng đúng 10 chữ số)
        return cleanPhone.matches("^0[0-9]{9}$");
    }
}