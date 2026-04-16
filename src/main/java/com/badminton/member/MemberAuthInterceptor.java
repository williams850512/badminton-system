package com.badminton.member;

import com.badminton.admin.AdminBean;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class MemberAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        String uri = request.getRequestURI();

        // 1. 開放路徑 
        if (uri.contains("/login") || uri.contains("/register") || uri.contains("/logout") || uri.equals("/")) {
            return true;
        }

        // 2. 後台管理員權限驗證
        if (uri.contains("/api/admin")) {
            AdminBean admin = (session != null) ? (AdminBean) session.getAttribute("adminUser") : null;
            if (admin == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "管理員權限不足，請重新登入");
                return false;
            }
            return true;
        }

        // 3. 前台會員權限驗證
        if (uri.contains("/api/member")) {
            AdminBean admin = (session != null) ? (AdminBean) session.getAttribute("adminUser") : null;
            MemberBean user = (session != null) ? (MemberBean) session.getAttribute("user") : null;
            
            if (admin == null && user == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "請先登入會員");
                return false;
            }
            return true;
        }

        return true;
    }
}
