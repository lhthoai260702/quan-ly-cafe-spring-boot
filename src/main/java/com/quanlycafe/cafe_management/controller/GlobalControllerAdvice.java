package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.UserProfileDTO;
import com.quanlycafe.cafe_management.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

/**
 * GlobalControllerAdvice
 * Version 1.0
 * Date: 29-05-2026
 * Modification Logs:
 * DATE       AUTHOR       DESCRIPTION
 * -----------------------------------------------------------------------
 * 29-05-2026 lhthoai       Create
 */
@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private ProfileService profileService;

    /**
     * Lấy thông tin người dùng hiện tại
     *
     * @param principal Principal
     * @return UserProfileDTO
     */
    @ModelAttribute("currentUser")
    public UserProfileDTO getCurrentUser(Principal principal) {
        if (principal != null) {
            return profileService.getCurrentUserProfile();
        }

        return new UserProfileDTO();
    }
}