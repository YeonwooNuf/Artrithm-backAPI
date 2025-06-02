package com.artrithm.backendapi.websocket;


import com.artrithm.backendapi.dto.AuctionBidDto;
import com.artrithm.backendapi.service.AuctionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AuctionWebSocketHandler extends TextWebSocketHandler {

    private final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());
    private final AuctionService auctionService;

    public AuctionWebSocketHandler(AuctionService auctionService) {
        this.auctionService = auctionService;
    }


    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("✅ 연결됨: " + session.getId());
    }


    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            System.out.println("📩 수신 메시지: " + message.getPayload());

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> data = objectMapper.readValue(message.getPayload(), Map.class);

            Long auctionId = Long.valueOf(data.get("auctionId").toString());
            String userId = data.get("userId").toString();
            int price = Integer.parseInt(data.get("price").toString());

            // 👉 핵심: 서비스에 위임
            auctionService.updateTop3(auctionId, userId, price);
            AuctionBidDto top = auctionService.getTop3(auctionId);

            if (!data.containsKey("price") || !(data.get("price") instanceof Number)) {
                System.err.println("❌ 잘못된 입찰 데이터: " + message.getPayload());
                return;
            }

            for (WebSocketSession s : sessions) {
                if (s.isOpen()) {
                    String response = "{\"price\":" + top.getTop1Price() + "}";
                    s.sendMessage(new TextMessage(response));
                    System.out.println("📤 전송 메시지: " + response);
                }
            }
        } catch (Exception e) {
            System.err.println("🧨 handleTextMessage 예외 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        System.out.println("❌ 연결 종료: " + session.getId());
    }
}

