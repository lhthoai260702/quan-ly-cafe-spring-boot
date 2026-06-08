package com.quanlycafe.cafe_management.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * ValidPhoneNumber Annotation
 * Version 1.0
 * Date: 30-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 */
@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhoneNumber {
    String message() default "Số điện thoại không đúng định dạng (VD: 0912345678)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}