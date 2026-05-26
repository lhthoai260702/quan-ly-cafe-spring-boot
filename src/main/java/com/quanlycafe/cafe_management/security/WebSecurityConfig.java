package com.quanlycafe.cafe_management.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Mã hóa mật khẩu chuẩn
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll() // Cho phép tải tài nguyên tĩnh
                        .anyRequest().authenticated() // Mọi trang khác đều phải đăng nhập
                )
                .formLogin(form -> form
                        .loginPage("/login") // Mở trang giao diện của mình
                        .loginProcessingUrl("/login") // Nơi Spring xử lý form Submit
                        .defaultSuccessUrl("/dashboard", true) // Chuyển đến dashboard khi thành công
                        .failureUrl("/login?error=true") // Trả về lỗi nếu sai mật khẩu
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // Khớp với th:action="@{/logout}" trong thẻ form HTML
                        .logoutSuccessUrl("/login?logout") // Trả về trang đăng nhập kèm thông báo sau khi đăng xuất thành công
                        .invalidateHttpSession(true) // Hủy toàn bộ phiên làm việc (Session) của người dùng hiện tại
                        .deleteCookies("JSESSIONID") // Xóa Cookie chứa thông tin định danh trên trình duyệt
                        .permitAll()
                );

        return http.build();
    }
}