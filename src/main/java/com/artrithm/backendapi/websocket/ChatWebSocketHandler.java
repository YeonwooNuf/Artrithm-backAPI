package com.artrithm.backendapi.websocket;

import com.artrithm.backendapi.model.ChatMessage;
import com.artrithm.backendapi.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatMessageRepository chatMessageRepository;
    private final Map<String, WebSocketSession> sessions = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String roomId = getRoomIdFromQuery(session);
        sessions.put(session.getId(), session);
        System.out.println("✅ 채팅 연결됨: " + session.getId() + " (roomId: " + roomId + ")");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload(); // JSON 문자열
        // (예: {"roomId":"abc123", "senderId":1, "senderRole":"viewer", "message":"안녕하세요"})

        // 실제로는 Jackson 사용해 JSON 파싱 (간단화 버전)
        System.out.println("💬 메시지 수신: " + payload);

        // 메시지를 MongoDB에 저장 (예시)
        ChatMessage chatMessage = ChatMessage.builder()
                .roomId("roomId") // 실제 파싱 필요
                .senderId(1L)
                .senderRole("viewer")
                .message(payload)
                .sentAt(LocalDateTime.now())
                .build();
        chatMessageRepository.save(chatMessage);

        // 전체 사용자에게 메시지 브로드캐스트 (단일 roomId라면 필터 가능)
        for (WebSocketSession s : sessions.values()) {
            if (s.isOpen()) {
                s.sendMessage(message);
            }
        }
    }

    private String getRoomIdFromQuery(WebSocketSession session) {
        // ?roomId=xxx 같은 파라미터 파싱
        String query = session.getUri().getQuery();
        if (query != null && query.startsWith("roomId=")) {
            return query.substring(7);
        }
        return null;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session.getId());
    }
}
