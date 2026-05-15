package com.badminton.payment;

import com.badminton.config.LinePayConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

/**
 * LINE Pay API v3 串接核心服務
 * 
 * 負責：
 * 1. 組裝 HMAC-SHA256 數位簽章 (LINE Pay 規定的驗證機制)
 * 2. 呼叫 Request API  → 取得付款網址
 * 3. 呼叫 Confirm API  → 確認扣款
 * 
 * ★ 其他組員不需要改這個檔案！
 *   他們只需要透過 PaymentController 傳入 orderId + amount + productName 即可。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LinePayService {

    private final LinePayConfig linePayConfig;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestTemplate restTemplate = new RestTemplate();

    // =====================================================================
    // 1. Request API — 向 LINE Pay 申請一筆付款
    // =====================================================================
    /**
     * 向 LINE Pay 發起付款請求
     *
     * @param orderId     訂單編號（含前綴，例如 "ORD-123"）
     * @param amount      付款金額
     * @param productName 商品名稱（會顯示在 LINE Pay 畫面上）
     * @return LinePayResponse，內含 transactionId 和付款網址
     */
    public LinePayResponse requestPayment(String orderId, int amount, String productName) throws Exception {
        String apiPath = "/v3/payments/request";

        // 組裝 Request Body (符合 LINE Pay API v3 規格)
        LinePayRequestBody body = LinePayRequestBody.builder()
                .amount(amount)
                .currency("TWD")
                .orderId(orderId)
                .packages(java.util.List.of(
                        LinePayRequestBody.ProductPackage.builder()
                                .id("pkg-001")
                                .amount(amount)
                                .name("羽過天晴商品訂單")
                                .products(java.util.List.of(
                                        LinePayRequestBody.ProductItem.builder()
                                                .name(productName)
                                                .quantity(1)
                                                .price(amount)
                                                .build()
                                ))
                                .build()
                ))
                .redirectUrls(LinePayRequestBody.RedirectUrls.builder()
                        .confirmUrl(linePayConfig.getConfirmUrl() + "?orderId=" + orderId)
                        .cancelUrl(linePayConfig.getCancelUrl())
                        .build())
                .build();

        String bodyJson = objectMapper.writeValueAsString(body);
        log.info("[LINE Pay] Request Body: {}", bodyJson);

        // 組裝 HTTP Headers (含 HMAC-SHA256 簽章)
        HttpHeaders headers = createHeaders(apiPath, bodyJson);
        HttpEntity<String> entity = new HttpEntity<>(bodyJson, headers);

        // 呼叫 LINE Pay API
        String url = linePayConfig.getApiUrl() + apiPath;
        ResponseEntity<LinePayResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, LinePayResponse.class);

        log.info("[LINE Pay] Request Response: {}", response.getBody());
        return response.getBody();
    }

    // =====================================================================
    // 2. Confirm API — 確認扣款（使用者在 LINE Pay 上同意付款後呼叫）
    // =====================================================================
    /**
     * 確認 LINE Pay 扣款
     *
     * @param transactionId LINE Pay 交易 ID（從 Request API 回傳取得）
     * @param amount        確認金額（必須與 Request 時一致）
     * @return LinePayResponse
     */
    public LinePayResponse confirmPayment(long transactionId, int amount) throws Exception {
        String apiPath = "/v3/payments/" + transactionId + "/confirm";

        // 組裝 Confirm Body
        LinePayConfirmBody body = LinePayConfirmBody.builder()
                .amount(amount)
                .currency("TWD")
                .build();

        String bodyJson = objectMapper.writeValueAsString(body);
        log.info("[LINE Pay] Confirm Body: {}", bodyJson);

        // 組裝 HTTP Headers (含 HMAC-SHA256 簽章)
        HttpHeaders headers = createHeaders(apiPath, bodyJson);
        HttpEntity<String> entity = new HttpEntity<>(bodyJson, headers);

        // 呼叫 LINE Pay API
        String url = linePayConfig.getApiUrl() + apiPath;
        ResponseEntity<LinePayResponse> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, LinePayResponse.class);

        log.info("[LINE Pay] Confirm Response: {}", response.getBody());
        return response.getBody();
    }

    // =====================================================================
    // 3. HMAC-SHA256 數位簽章 — LINE Pay 的核心驗證機制
    // =====================================================================
    /**
     * 產生 LINE Pay API 需要的 HTTP Headers
     * 
     * LINE Pay v3 規定每次請求都要帶上 HMAC-SHA256 簽章，
     * 簽章內容 = Channel Secret + API Path + Request Body + Nonce
     */
    private HttpHeaders createHeaders(String apiPath, String bodyJson) throws Exception {
        String nonce = UUID.randomUUID().toString();

        // 簽章原始字串 = ChannelSecret + apiPath + bodyJson + nonce
        String signatureData = linePayConfig.getChannelSecret() + apiPath + bodyJson + nonce;

        // 用 HMAC-SHA256 加密
        String signature = hmacSha256(linePayConfig.getChannelSecret(), signatureData);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-LINE-ChannelId", linePayConfig.getChannelId());
        headers.set("X-LINE-Authorization-Nonce", nonce);
        headers.set("X-LINE-Authorization", signature);
        return headers;
    }

    /**
     * HMAC-SHA256 加密並回傳 Base64 編碼的結果
     */
    private String hmacSha256(String key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(
                key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}
