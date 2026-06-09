package com.quanlycafe.cafe_management.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * WebSecurityConfig
 * Version 1.2
 * Date: 09-06-2026
 * Modification Logs:
 * DATE         AUTHOR      DESCRIPTION
 * 29-05-2026   lhthoai     Create
 * 09-06-2026   lhthoai     Thêm cấu hình Ghi nhớ đăng nhập (Remember Me)
 * 09-06-2026   lhthoai     Apply Java Coding Convention
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Khai báo Bean mã hóa mật khẩu sử dụng BCrypt
     *
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Cấu hình chuỗi lọc bảo mật cho ứng dụng
     *
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception lỗi cấu hình bảo mật
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // CÁC TRANG DÀNH RIÊNG CHO QUẢN LÝ (ROLE_ADMIN)
                        .requestMatchers("/employee/**", "/equipment/**", "/inventory/**",
                                "/menu/**", "/marketing/**", "/budget/**",
                                "/data/**", "/report/**", "/settings/**").hasRole("ADMIN")

                        // CÁC TRANG CÒN LẠI - YÊU CẦU ĐĂNG NHẬP
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/home", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                // CẤU HÌNH TÍNH NĂNG GHI NHỚ ĐĂNG NHẬP
                .rememberMe(remember -> remember
                        .key("CafeHarmonySecretKey2026")
                        .tokenValiditySeconds(7 * 24 * 60 * 60)
                        .userDetailsService(customUserDetailsService)
                        .rememberMeParameter("remember-me")
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll()
                );

        return http.build();
    }

}