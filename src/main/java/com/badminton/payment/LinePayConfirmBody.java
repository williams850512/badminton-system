package com.badminton.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LINE Pay Confirm API 的請求 Body
 * 
 * 前端付款後回傳 transactionId，後端用此 Body 去 Confirm 扣款。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LinePayConfirmBody {

    /** 確認金額 (必須與 Request 時的金額一致) */
    private Integer amount;

    /** 幣別 */
    private String currency;
}
