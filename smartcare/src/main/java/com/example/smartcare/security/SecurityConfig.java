package com.example.smartcare.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                // 🛠️ CHÌA KHÓA 1: Giải quyết lỗi 403 "Ảo" của Spring Boot
                .requestMatchers("/error").permitAll()
                
                // 🛠️ CHÌA KHÓA 2: Mở đường cho tất cả các file HTML tĩnh (Phòng hờ lỗi đuôi .html)
                .requestMatchers("/**/*.html").permitAll()

                // ✅ Public pages - Không cần token
                .requestMatchers("/", "/login", "/register", "/home").permitAll()
                
                // ✅ Dashboard pages - Template render public (data load via API with JWT)
                .requestMatchers("/patient/**", "/doctor/**", "/admin/**").permitAll()
                
                // ✅ Public APIs - Auth endpoints (login, register)
                .requestMatchers("/api/v1/auth/**").permitAll()
                
                // ✅ Public API - Search doctors without auth
                .requestMatchers("/api/v1/users/doctors/search").permitAll()
                
                // ✅ Static resources - CSS, JS, images
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                
                // ✅ Uploads folder
                .requestMatchers("/uploads/**").permitAll()
                
                // 🔒 Tất cả request khác - BẮT BUỘC CÓ TOKEN
                .anyRequest().authenticated() 
            )
            // Session: Không lưu (dùng JWT thay vì session)
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Authentication provider
            .authenticationProvider(authenticationProvider)
            
            // JWT filter - đứng trước UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}