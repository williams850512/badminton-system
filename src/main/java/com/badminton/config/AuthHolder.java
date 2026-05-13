package com.badminton.config;

/**
 * 當前登入使用者的 Holder（基於 ThreadLocal）
 *
 * 使用方式（在任何 Controller / Service 裡）：
 *   Integer userId = AuthHolder.getUserId();
 *   String username = AuthHolder.getUsername();
 *   String role = AuthHolder.getRole();
 *
 *   // 判斷身份
 *   if (AuthHolder.isMember()) { ... }
 *   if (AuthHolder.isManager()) { ... }
 *
 * 生命週期：
 *   MemberAuthInterceptor.preHandle()     → 設定（從 JWT 解析）
 *   MemberAuthInterceptor.afterCompletion() → 清除（避免 Thread 重用導致洩漏）
 */
public class AuthHolder {

    private static final ThreadLocal<Integer> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE = new ThreadLocal<>();

    // ===== 設定（由 Interceptor 呼叫）=====
    public static void set(Integer userId, String username, String role) {
        USER_ID.set(userId);
        USERNAME.set(username);
        ROLE.set(role);
    }

    // ===== 清除（由 Interceptor 呼叫）=====
    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
        ROLE.remove();
    }

    // ===== 取得資訊 =====
    public static Integer getUserId() {
        return USER_ID.get();
    }

    public static String getUsername() {
        return USERNAME.get();
    }

    public static String getRole() {
        return ROLE.get();
    }

    // ===== 角色判斷 =====
    public static boolean isManager() {
        return "MANAGER".equals(ROLE.get());
    }

    public static boolean isStaff() {
        return "STAFF".equals(ROLE.get());
    }

    public static boolean isAdmin() {
        return isManager() || isStaff();
    }

    public static boolean isMember() {
        return "MEMBER".equals(ROLE.get());
    }

    public static boolean isLoggedIn() {
        return USER_ID.get() != null;
    }
}
