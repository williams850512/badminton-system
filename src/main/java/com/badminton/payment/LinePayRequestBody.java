package com.badminton.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * LINE Pay Request API 的請求 Body
 * 
 * 對應 LINE Pay API v3 的 /v3/payments/request
 * 文件: https://pay.line.me/documents/online_v3.html
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinePayRequestBody {

    /** 訂單金額 */
    private Integer amount;

    /** 幣別 (TWD) */
    private String currency;

    /** 訂單編號 (帶前綴，例如 ORD-123、BKG-456) */
    private String orderId;

    /** 商品列表 */
    private List<ProductPackage> packages;

    /** 付款成功後的回傳設定 */
    private RedirectUrls redirectUrls;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductPackage {
        /** Package ID */
        private String id;

        /** 此 Package 的總金額 */
        private Integer amount;

        /** Package 名稱 */
        private String name;

        /** 商品列表 */
        private List<ProductItem> products;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductItem {
        /** 商品名稱 */
        private String name;

        /** 數量 */
        private Integer quantity;

        /** 單價 */
        private Integer price;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RedirectUrls {
        /** 付款成功回傳 URL */
        private String confirmUrl;

        /** 付款取消回傳 URL */
        private String cancelUrl;
    }
}
