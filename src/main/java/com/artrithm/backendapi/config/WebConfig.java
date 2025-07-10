package com.artrithm.backendapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173", "http://192.168.0.56:5173",
                        "http://172.30.1.54:5173", "http://192.168.10.159:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true); // 세션 쿠키 허용
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadDir = System.getenv("UPLOAD_DIR");
        if (uploadDir == null || uploadDir.isBlank()) {
            uploadDir = System.getProperty("user.dir") + "/uploads"; // 예: backend-api/uploads
        }

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
    }
}
