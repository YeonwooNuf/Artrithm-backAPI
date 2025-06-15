package com.artrithm.backendapi.websocket;

import com.artrithm.backendapi.model.ChatMessage;
import com.artrithm.backendapi.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatMessageRepository chatMessageRepository;
    private final Map<String, List<WebSocketSession>> roomSessions = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String roomId = getRoomIdFromQuery(session);
        if (roomId != null) {
            roomSessions.computeIfAbsent(roomId, k -> new ArrayList<>()).add(session);
            System.out.println("✅ 채팅 연결됨: " + session.getId() + " (roomId: " + roomId + ")");
        } else {
            System.out.println("❌ roomId 없음, 연결 거부됨");
            try {
                session.close(CloseStatus.BAD_DATA);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // JSON 파싱
        Map<String, Object> data = objectMapper.readValue(message.getPayload(), Map.class);
        String roomId = (String) data.get("roomId");
        Long senderId = Long.parseLong(data.get("senderId").toString());
        String senderRole = (String) data.get("senderRole");
        String content = (String) data.get("message");

        // 메시지 생성 및 저장
        ChatMessage chatMessage = ChatMessage.builder()
                .roomId(roomId)
                .senderId(senderId)
                .senderRole(senderRole)
                .message(content)
                .sentAt(LocalDateTime.now())
                .build();
        chatMessageRepository.save(chatMessage);

        // 해당 roomId 세션에만 전송
        List<WebSocketSession> room = roomSessions.get(roomId);
        if (room != null) {
            String broadcast = objectMapper.writeValueAsString(chatMessage);
            for (WebSocketSession s : room) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(broadcast));
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // 모든 room에서 세션 제거
        roomSessions.values().forEach(sessions -> sessions.remove(session));
        System.out.println("❎ 채팅 종료됨: " + session.getId());
    }

    private String getRoomIdFromQuery(WebSocketSession session) {
        String query = session.getUri() != null ? session.getUri().getQuery() : null;
        if (query != null && query.startsWith("roomId=")) {
            return query.substring(7);
        }
        return null;
    }
}
