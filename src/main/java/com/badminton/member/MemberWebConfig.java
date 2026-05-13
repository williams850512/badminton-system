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

    // 1. 註�??�截??
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(memberAuthInterceptor)
                .addPathPatterns("/api/admins/**", "/api/members/**") // ?�截?�?��?�?API
                .excludePathPatterns(
                    "/api/admins/login",
                    "/api/admins/logout",
                    "/api/members/login", 
                    "/api/members/register",
                    "/api/members/logout",
                    "/api/members/reset-password",
                    "/api/members/send-verification-code",
                    "/api/members/google-login",
                    "/api/members/search",  // 後台搜尋會員功能需要
                    "/css/**", "/js/**", "/images/**", "/lib/**" // 放行靜態資源
                );
    }

    // 2. 跨�??�置
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // ?��??�??API 路�?
                .allowedOriginPatterns("*") // ?�許?�?��?�?(?�發?��?建議，�?線�??�特定�???
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true) // ?�許?�帶 Cookie (Session 驗�?必�?)
                .maxAge(3600);
    }
}
