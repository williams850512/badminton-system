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

        // ✅ 1. 開放路徑 (完全還原：登入、註冊、登出、首頁等不攔截)
        if (uri.contains("/login") || uri.contains("/register") || uri.contains("/logout") || uri.equals("/")) {
            return true;
        }

        // ✅ 2. 後台管理員權限驗證 (/api/admin/**)
        if (uri.contains("/api/admin")) {
            AdminBean admin = (session != null) ? (AdminBean) session.getAttribute("adminUser") : null;
            if (admin == null) {
                // 如果是管理員 API 但未登入，回傳 401
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "管理員權限不足，請重新登入");
                return false;
            }
            return true;
        }

        // ✅ 3. 前台會員權限驗證 (/api/members/**)
        if (uri.contains("/api/members")) {
            // 💡 完整化邏輯：管理員登入時，應可存取會員 API；否則檢查一般會員登入
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
