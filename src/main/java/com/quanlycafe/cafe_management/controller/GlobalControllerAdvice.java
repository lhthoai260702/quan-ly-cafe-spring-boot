package com.quanlycafe.cafe_management.controller;

import com.quanlycafe.cafe_management.dto.UserProfileDTO;
import com.quanlycafe.cafe_management.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.security.Principal;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private ProfileService profileService;

    @ModelAttribute("currentUser")
    public UserProfileDTO getCurrentUser(Principal principal) {
        if (principal != null) {
            return profileService.getCurrentUserProfile();
        }
        return new UserProfileDTO();
    }
}