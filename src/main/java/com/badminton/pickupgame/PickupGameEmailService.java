package com.badminton.pickupgame;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 🌟 臨打揪團專用的 Email 服務
 * 獨立於 member 套件的 EmailService，避免改到其他組員的程式碼
 */
@Service
public class PickupGameEmailService {

    @Autowired
    private JavaMailSender mailSender;

    /**
     * 團主群發公告：逐一寄送 Email 給已報名的球友
     * 使用 for-each 逐封寄送，單封失敗不影響其他人
     *
     * @param recipients 收件人 Email 清單
     * @param hostName   團主姓名（用於信件署名）
     * @param gameInfo   球局摘要（日期+時段+場地）
     * @param content    團主輸入的公告內容
     * @return 成功寄送的封數
     */
    public int sendBroadcast(List<String> recipients, String hostName,
                             String gameInfo, String content) {
        int successCount = 0;
        for (String toEmail : recipients) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom("ygtq.badminton@gmail.com");
                message.setTo(toEmail);
                message.setSubject("【羽過天晴】您報名的球局有最新公告");
                message.setText(
                    "Hi，球友您好！\n\n" +
                    "您報名的球局「" + gameInfo + "」，團主有新的公告：\n\n" +
                    "──────────────\n" +
                    content + "\n" +
                    "──────────────\n\n" +
                    "發佈者：" + hostName + "\n" +
                    "如有任何問題，請直接聯繫團主。\n\n" +
                    "— 羽過天晴羽球館"
                );
                mailSender.send(message);
                successCount++;
            } catch (Exception e) {
                // 單封寄失敗不中斷迴圈，記錄錯誤後繼續
                System.err.println("寄送公告信失敗 → " + toEmail + "：" + e.getMessage());
            }
        }
        return successCount;
    }
}
