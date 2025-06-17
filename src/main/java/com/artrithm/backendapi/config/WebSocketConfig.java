package com.artrithm.backendapi.config;

import com.artrithm.backendapi.service.AuctionService;
import com.artrithm.backendapi.repository.ChatMessageRepository;
import com.artrithm.backendapi.websocket.AuctionWebSocketHandler;
import com.artrithm.backendapi.websocket.ChatWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration(proxyBeanMethods = false)
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final AuctionService auctionService;
    private final ChatMessageRepository chatMessageRepository;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 경매 WebSocket
        registry.addHandler(new AuctionWebSocketHandler(auctionService), "/ws/auction")
                .setAllowedOrigins("*");

        // 채팅 WebSocket
        registry.addHandler(new ChatWebSocketHandler(chatMessageRepository), "/ws/chat")
                .setAllowedOrigins("*");
    }
}
