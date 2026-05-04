package com.badminton.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

/**
 * 全域 CORS 設定
 * 
 * 讓前端 Vue 開發伺服器（預設 localhost:5173）
 * 能夠跨域呼叫後端 /api/** 的 REST API。
 */
@Configuration
public class WebCorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // ① 允許的前端來源（開發階段）
        config.setAllowedOrigins(List.of(
            "http://localhost:5173",  // Vite 預設 port
            "http://localhost:3000"   // 備用 port
        ));

        // ② 允許的 HTTP 方法
        config.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // ③ 允許的請求標頭
        config.setAllowedHeaders(List.of("*"));

        // ④ 允許瀏覽器攜帶 Cookie（Session 驗證需要）
        config.setAllowCredentials(true);

        // ⑤ 預檢請求快取時間（秒），避免頻繁 OPTIONS 請求
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 只對 /api/** 路徑啟用 CORS
        source.registerCorsConfiguration("/api/**", config);
        // 上傳檔案的路徑也需要（圖片存取）
        source.registerCorsConfiguration("/uploads/**", config);

        return new CorsFilter(source);
    }
}
