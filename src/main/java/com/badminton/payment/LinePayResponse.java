package com.badminton.payment;

import lombok.Data;
import java.util.List;

/**
 * LINE Pay API 回應物件
 * 
 * 通用於 Request API 和 Confirm API 的回應格式。
 */
@Data
public class LinePayResponse {

    /** 回傳代碼，"0000" 表示成功 */
    private String returnCode;

    /** 回傳訊息 */
    private String returnMessage;

    /** 回傳的資料內容 */
    private Info info;

    /** 判斷是否成功 */
    public boolean isSuccess() {
        return "0000".equals(returnCode);
    }

    @Data
    public static class Info {
        // ===== Request API 回傳的欄位 =====

        /** LINE Pay 交易 ID（後續 Confirm 時需要） */
        private Long transactionId;

        /** 付款網址（前端要跳轉到這裡） */
        private PaymentUrl paymentUrl;

        // ===== Confirm API 回傳的欄位 =====

        /** 訂單編號 */
        private String orderId;

        /** 付款資訊 */
        private List<PayInfo> payInfo;
    }

    @Data
    public static class PaymentUrl {
        /** Web 付款頁面 URL */
        private String web;

        /** App 付款頁面 URL */
        private String app;
    }

    @Data
    public static class PayInfo {
        /** 付款方式 */
        private String method;

        /** 付款金額 */
        private Integer amount;
    }
}
