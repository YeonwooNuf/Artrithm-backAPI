package com.artrithm.backendapi.websocket;

import com.artrithm.backendapi.model.ChatMessage;
import com.artrithm.backendapi.model.ChatRoom;
import com.artrithm.backendapi.repository.ChatMessageRepository;
import com.artrithm.backendapi.repository.ChatRoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final Map<String, List<WebSocketSession>> roomSessions = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String roomId = getRoomIdFromQuery(session);
        System.out.println("🔌 afterConnectionEstablished - session ID: " + session.getId());
        System.out.println("🔎 연결 요청 쿼리: " + session.getUri());

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
        System.out.println("💬 수신된 메시지: " + message.getPayload());

        try {
            // JSON 파싱
            Map<String, Object> data = objectMapper.readValue(message.getPayload(), Map.class);
            String roomId = (String) data.get("roomId");
            Long senderId = Long.parseLong(data.get("senderId").toString());
            String senderRole = (String) data.get("senderRole");
            String content = (String) data.get("message");

            System.out.println("📦 파싱된 데이터 - roomId: " + roomId + ", senderId: " + senderId + ", senderRole: " + senderRole + ", message: " + content);

            // ✅ 수신자 조회 및 로그
            ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                    .orElseThrow(() -> new RuntimeException("❌ 채팅방 정보 없음: " + roomId));

            Long receiverId = chatRoom.getArtistId().equals(senderId)
                    ? chatRoom.getViewerId()
                    : chatRoom.getArtistId();

            System.out.println("📩 메시지 수신자 ID: " + receiverId);

            // 메시지 생성 및 저장
            ChatMessage chatMessage = ChatMessage.builder()
                    .roomId(roomId)
                    .senderId(senderId)
                    .senderRole(senderRole)
                    .message(content)
                    .sentAt(LocalDateTime.now())
                    .build();
            chatMessageRepository.save(chatMessage);
            System.out.println("📝 메시지 저장 완료");

            // 해당 roomId 세션에만 전송
            List<WebSocketSession> room = roomSessions.get(roomId);
            if (room != null) {
                String broadcast = objectMapper.writeValueAsString(chatMessage);
                for (WebSocketSession s : room) {
                    if (s.isOpen()) {
                        s.sendMessage(new TextMessage(broadcast));
                    }
                }
                System.out.println("📤 메시지 전송 완료 to room: " + roomId);
            } else {
                System.out.println("⚠️ 대상 roomId 세션이 존재하지 않음: " + roomId);
            }
        } catch (Exception e) {
            System.out.println("❌ 메시지 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // 모든 room에서 세션 제거
        roomSessions.values().forEach(sessions -> sessions.remove(session));
        System.out.println("❎ 채팅 종료됨: " + session.getId() + " - 상태: " + status);
    }

    private String getRoomIdFromQuery(WebSocketSession session) {
        String query = session.getUri() != null ? session.getUri().getQuery() : null;
        System.out.println("🔍 getRoomIdFromQuery - 쿼리 문자열: " + query);
        if (query != null && query.startsWith("roomId=")) {
            return query.substring(7);
        }
        return null;
    }
}
