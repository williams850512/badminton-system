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

    // 1. è¨»å??”æˆª??
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(memberAuthInterceptor)
                .addPathPatterns("/api/admins/**", "/api/members/**") // ?”æˆª?€?‰å?ç«?API
                .excludePathPatterns(
                    "/api/admins/login",
                    "/api/admins/logout",
                    "/api/members/login", 
                    "/api/members/register",
                    "/api/members/logout",
                    "/api/members/search",  // å¾Œå°?œå??ƒå“¡?Ÿèƒ½?€è¦?
                    "/css/**", "/js/**", "/images/**", "/lib/**" // ?¾è??œæ?è³‡æ?
                );
    }

    // 2. è·¨å??ç½®
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // ?å??€??API è·¯å?
                .allowedOriginPatterns("*") // ?è¨±?€?‰ä?æº?(?‹ç™¼?°å?å»ºè­°ï¼Œä?ç·šå??¹ç‰¹å®šå???
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true) // ?è¨±?œå¸¶ Cookie (Session é©—è?å¿…å?)
                .maxAge(3600);
    }
}
