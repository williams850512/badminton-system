package com.badminton.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/notifications")
public class NotificationController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 測試用的 API：呼叫這支 API 就會觸發 WebSocket 廣播給所有前端
    @PostMapping("/send-test")
    public String sendTestNotification() {
        NotificationMessage msg = new NotificationMessage(
                UUID.randomUUID().toString(),
                "系統測試通知",
                "這是一則透過 WebSocket 推播的即時測試訊息！",
                LocalDateTime.now()
        );
        
        // 將訊息推播到 /topic/admin/notifications
        messagingTemplate.convertAndSend("/topic/admin/notifications", msg);
        
        return "廣播發送成功！";
    }
}
