package com.badminton.common;

import lombok.Data;

/**
 * 統一 API 回應格式
 * 
 * 所有 REST API 都透過這個類別包裝回傳值，
 * 讓前端只需要一套邏輯就能處理所有 API 的回應。
 * 
 * 回傳格式：
 * {
 *   "success": true/false,
 *   "message": "操作結果說明",
 *   "data": { 實際資料 }
 * }
 */
@Data
public class ApiResponse<T> {

    /** 是否成功 */
    private boolean success;

    /** 訊息（成功提示 或 錯誤原因） */
    private String message;

    /** 實際資料（成功時才有值） */
    private T data;

    // ===== 成功的靜態工廠方法 =====

    /** 成功：回傳資料 */
    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.success = true;
        resp.data = data;
        return resp;
    }

    /** 成功：回傳資料 + 自訂訊息 */
    public static <T> ApiResponse<T> ok(T data, String message) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.success = true;
        resp.data = data;
        resp.message = message;
        return resp;
    }

    /** 成功：只回傳訊息（不帶資料，適用於刪除、登出等操作） */
    public static <T> ApiResponse<T> ok(String message) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.success = true;
        resp.message = message;
        return resp;
    }

    // ===== 失敗的靜態工廠方法 =====

    /** 失敗：回傳錯誤訊息 */
    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> resp = new ApiResponse<>();
        resp.success = false;
        resp.message = message;
        return resp;
    }
}
