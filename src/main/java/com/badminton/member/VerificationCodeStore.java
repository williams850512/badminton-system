package com.badminton.member;

import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 驗證碼暫存器
 * 使用 ConcurrentHashMap 存放 email -> (驗證碼, 過期時間)
 * 驗證碼有效期為 5 分鐘
 */
@Component
public class VerificationCodeStore {

    private static class CodeEntry {
        String code;
        LocalDateTime expireAt;

        CodeEntry(String code, LocalDateTime expireAt) {
            this.code = code;
            this.expireAt = expireAt;
        }
    }

    private final ConcurrentHashMap<String, CodeEntry> store = new ConcurrentHashMap<>();

    /**
     * 儲存驗證碼，有效期 5 分鐘
     */
    public void save(String email, String code) {
        store.put(email.toLowerCase(), new CodeEntry(code, LocalDateTime.now().plusMinutes(5)));
    }

    /**
     * 驗證碼是否正確且未過期
     */
    public boolean verify(String email, String code) {
        CodeEntry entry = store.get(email.toLowerCase());
        if (entry == null) return false;
        if (LocalDateTime.now().isAfter(entry.expireAt)) {
            store.remove(email.toLowerCase()); // 已過期，移除
            return false;
        }
        return entry.code.equals(code);
    }

    /**
     * 驗證成功後移除驗證碼（一次性使用）
     */
    public void remove(String email) {
        store.remove(email.toLowerCase());
    }
}
