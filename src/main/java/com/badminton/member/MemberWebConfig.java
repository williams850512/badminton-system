package com.badminton.member;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
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
                .addPathPatterns("/api/admins/**", "/api/members/**", "/api/bookings/my-bookings", "/api/bookings/my-all-bookings") // 攔截需要 JWT 的 API
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

}
