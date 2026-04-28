package com.badminton.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MemberWebConfig implements WebMvcConfigurer {

    @Autowired
    private MemberAuthInterceptor memberAuthInterceptor;

    // 1. 註冊攔截器
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(memberAuthInterceptor)
                .addPathPatterns("/api/admins/**", "/api/members/**") // 攔截所有後端 API
                .excludePathPatterns(
                    "/api/admins/login",
                    "/api/admins/logout",
                    "/api/members/login", 
                    "/api/members/register",
                    "/api/members/logout",
                    "/css/**", "/js/**", "/images/**", "/lib/**" // 放行靜態資源
                );
    }

    // 2. 跨域配置
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 針對所有 API 路徑
                .allowedOriginPatterns("*") // 允許所有來源 (開發環境建議，上線再改特定域名)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true) // 允許攜帶 Cookie (Session 驗證必備)
                .maxAge(3600);
    }
}
