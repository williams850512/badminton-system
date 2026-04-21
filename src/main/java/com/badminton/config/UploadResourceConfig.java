package com.badminton.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 設定外部上傳目錄的靜態資源映射
 * 讓 /uploads/** 的 URL 能對應到實體檔案系統的上傳目錄
 */
@Configuration
public class UploadResourceConfig implements WebMvcConfigurer {

    @Value("${app.upload.dir:./uploads/products}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 取得上傳目錄的絕對路徑
        String absolutePath = new java.io.File(uploadDir).getAbsoluteFile().toURI().toString();

        // 將 /uploads/products/** URL 映射到實體檔案目錄
        registry.addResourceHandler("/uploads/products/**")
                .addResourceLocations(absolutePath);
    }
}
