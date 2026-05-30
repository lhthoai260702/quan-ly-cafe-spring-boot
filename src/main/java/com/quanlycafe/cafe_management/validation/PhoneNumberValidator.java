package com.quanlycafe.cafe_management.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * PhoneNumberValidator
 * Version 1.0
 * Date: 30-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lthoai       Create
 */
public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    @Override
    public void initialize(ValidPhoneNumber constraintAnnotation) {
    }

    @Override
    public boolean isValid(String phoneField, ConstraintValidatorContext context) {
        if (phoneField == null || phoneField.trim().isEmpty()) {
            return false;
        }
        // Biểu thức chính quy: Bắt đầu bằng 0, theo sau là 3,5,7,8,9 và đúng 8 số nữa
        return phoneField.matches("^(0[3|5|7|8|9])+([0-9]{8})$");
    }
}