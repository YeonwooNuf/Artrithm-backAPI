package com.artrithm.backendapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173") // React dev 서버 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true); // ✅ 세션 쿠키 허용
    }
}
