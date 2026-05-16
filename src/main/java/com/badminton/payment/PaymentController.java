package com.badminton.payment;

import com.badminton.booking.BookingService;
import com.badminton.booking.BookingStatus;
import com.badminton.order.Order;
import com.badminton.order.OrderService;
import com.badminton.order.OrderStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 金流控制器 — 共用金流 API
 * 
 * 提供 LINE Pay 的 Request（申請付款）和 Confirm（確認扣款）API。
 * 
 * ★ 其他組員串接指南：
 *   呼叫 POST /api/payment/linepay/request 時，傳入以下 JSON：
 *   {
 *     "orderId": "ORD-123",     ← 帶上你業務的前綴 (ORD / BKG / PKG)
 *     "amount": 5950,           ← 付款金額
 *     "productName": "羽球商品"  ← 顯示在 LINE Pay 畫面上的名稱
 *   }
 *   
 *   系統會回傳 LINE Pay 的付款網址，前端跳轉過去即可。
 */
@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final LinePayService linePayService;
    private final OrderService orderService;
    private final BookingService bookingService;

    // =====================================================================
    // LINE Pay — 申請付款（前端呼叫此 API 取得 LINE Pay 付款網址）
    // =====================================================================
    @PostMapping("/linepay/request")
    public ResponseEntity<?> linePayRequest(@RequestBody Map<String, Object> request) {
        try {
            String orderId = (String) request.get("orderId");
            int amount = (int) request.get("amount");
            String productName = (String) request.getOrDefault("productName", "羽過天晴商品");

            log.info("[Payment] LINE Pay Request — orderId: {}, amount: {}", orderId, amount);

            LinePayResponse response = linePayService.requestPayment(orderId, amount, productName);

            if (response != null && response.isSuccess()) {
                // 回傳付款網址和 transactionId 給前端
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "paymentUrl", response.getInfo().getPaymentUrl().getWeb(),
                        "transactionId", response.getInfo().getTransactionId()
                ));
            } else {
                String msg = response != null ? response.getReturnMessage() : "未知錯誤";
                log.error("[Payment] LINE Pay Request failed: {}", msg);
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "LINE Pay 請求失敗：" + msg
                ));
            }
        } catch (Exception e) {
            log.error("[Payment] LINE Pay Request exception", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "伺服器錯誤：" + e.getMessage()
            ));
        }
    }

    // =====================================================================
    // LINE Pay — 確認扣款（LINE Pay 回傳後，前端呼叫此 API 完成付款）
    // =====================================================================
    @PostMapping("/linepay/confirm")
    public ResponseEntity<?> linePayConfirm(@RequestBody Map<String, Object> request) {
        try {
            long transactionId = Long.parseLong(String.valueOf(request.get("transactionId")));
            int amount = (int) request.get("amount");
            String orderId = (String) request.get("orderId");

            log.info("[Payment] LINE Pay Confirm — transactionId: {}, orderId: {}, amount: {}",
                    transactionId, orderId, amount);

            LinePayResponse response = linePayService.confirmPayment(transactionId, amount);

            if (response != null && response.isSuccess()) {
                // ★ 根據訂單前綴，自動更新對應業務的狀態
                updateBusinessStatus(orderId);

                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "付款成功！"
                ));
            } else {
                String msg = response != null ? response.getReturnMessage() : "未知錯誤";
                log.error("[Payment] LINE Pay Confirm failed: {}", msg);
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "LINE Pay 確認失敗：" + msg
                ));
            }
        } catch (Exception e) {
            log.error("[Payment] LINE Pay Confirm exception", e);
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "伺服器錯誤：" + e.getMessage()
            ));
        }
    }

    // =====================================================================
    // ★ 根據訂單前綴分流，更新各業務的付款狀態
    // =====================================================================
    /**
     * 其他組員要新增自己的業務分流，只需要在這裡加一個 case 即可！
     * 例如：
     *   case "BKG" → bookingService.markAsPaid(id);
     *   case "PKG" → pickupGameService.markAsPaid(id);
     */
    private void updateBusinessStatus(String orderId) {
        if (orderId == null || !orderId.contains("-")) {
            log.warn("[Payment] 無法解析 orderId: {}", orderId);
            return;
        }

        String prefix = orderId.split("-")[0]; // 例如 "ORD"
        String idStr = orderId.split("-")[1];   // 例如 "123"

        switch (prefix) {
            case "ORD" -> {
                // 商品訂單 — 更新為已付款
                Integer id = Integer.parseInt(idStr);
                Order order = orderService.getOrderById(id);
                if (order != null) {
                    orderService.updateOrder(id, OrderStatus.PAID, order.getPaymentType(), order.getNote());
                    log.info("[Payment] 訂單 #{} 已更新為 PAID", id);
                }
            }
            // ★ 場地預約 — LINE Pay 付款完成，維持 CONFIRMED 狀態
            case "BKG" -> {
                Integer bkgId = Integer.parseInt(idStr);
                bookingService.updateStatus(bkgId, BookingStatus.CONFIRMED);
                log.info("[Payment] 預約 #{} LINE Pay 付款完成", bkgId);
            }
            // case "PKG" -> { pickupGameService.markAsPaid(Integer.parseInt(idStr)); }
            default -> log.warn("[Payment] 未知的訂單前綴: {}", prefix);
        }
    }
}
