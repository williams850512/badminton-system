package com.badminton.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * LINE Pay Sandbox 設定
 * 
 * 從 application.yml 讀取 linepay.* 的設定值。
 * 其他組員不需要改這個檔案，只要在 yml 裡填入自己的金鑰即可。
 */
@Data
@Component
@ConfigurationProperties(prefix = "linepay")
public class LinePayConfig {

    /** LINE Pay Channel ID (通路 ID) */
    private String channelId;

    /** LINE Pay Channel Secret (通路密鑰) */
    private String channelSecret;

    /** LINE Pay API Base URL (Sandbox or Production) */
    private String apiUrl = "https://sandbox-api-pay.line.me";

    /** 前端付款成功後的回傳網址 */
    private String confirmUrl = "http://localhost:5173/payment/linepay/confirm";

    /** 前端取消付款的回傳網址 */
    private String cancelUrl = "http://localhost:5173/payment/linepay/cancel";
}
