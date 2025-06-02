package com.artrithm.backendapi.config;


import com.artrithm.backendapi.service.AuctionService;
import com.artrithm.backendapi.websocket.AuctionWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final AuctionService auctionService;

    public AuctionWebSocketHandler auctionWebSocketHandler() {
        return new AuctionWebSocketHandler(auctionService);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new AuctionWebSocketHandler(auctionService), "/ws/auction")
                .setAllowedOrigins("*");
    }
}
