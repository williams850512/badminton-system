package com.badminton.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 設定前端訂閱的廣播路徑前綴
        config.enableSimpleBroker("/topic");
        // 設定前端發送訊息到後端的路徑前綴
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 前端 WebSocket 連線的 Endpoint
        // setAllowedOriginPatterns("*") 解決跨域問題
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");
    }
}
