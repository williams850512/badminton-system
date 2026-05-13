package com.badminton.member;

import com.badminton.config.AuthHolder;
import com.badminton.config.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * JWT 驗證攔截器
 *
 * 從 Authorization: Bearer <token> 取出 JWT 並驗證，
 * 將解析出的使用者資訊存入：
 *   1. request attribute（供 Controller 直接取用）
 *   2. AuthHolder（靜態 ThreadLocal，供 Service 層取用）
 */
@Component
public class MemberAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // 1. 開放路徑（登入、註冊、登出不需要 Token）
        if (uri.contains("/login") || uri.contains("/register") || uri.contains("/logout") || uri.contains("/reset-password") || uri.equals("/")) {
            return true;
        }

        // 2. OPTIONS 預檢請求直接放行（CORS 需要）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // 3. 從 Header 取出 JWT
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "請先登入（缺少 Token）");
            return false;
        }

        String token = authHeader.substring(7); // 去掉 "Bearer "

        // 4. 驗證 JWT
        try {
            Claims claims = jwtUtil.parseToken(token);

            Integer userId = claims.get("userId", Integer.class);
            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            // 存入 request attribute（Controller 可用）
            request.setAttribute("jwtUserId", userId);
            request.setAttribute("jwtUsername", username);
            request.setAttribute("jwtRole", role);

            // 存入 AuthHolder（Service 層可用）
            AuthHolder.set(userId, username, role);

        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token 無效或已過期，請重新登入");
            return false;
        }

        // 5. 後台 API 權限檢查：/api/admin/** 需要 MANAGER 或 STAFF 角色
        if (uri.contains("/api/admin")) {
            String role = (String) request.getAttribute("jwtRole");
            if (!"MANAGER".equals(role) && !"STAFF".equals(role)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "管理員權限不足");
                return false;
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 請求結束後清除 ThreadLocal，避免 Thread 池重用導致資料洩漏
        AuthHolder.clear();
    }
}
