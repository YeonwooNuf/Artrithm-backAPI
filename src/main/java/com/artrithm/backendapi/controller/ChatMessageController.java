package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.model.ChatMessage;
import com.artrithm.backendapi.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageRepository chatMessageRepository;

    // 채팅방에 해당하는 메시지 전체 조회
    @GetMapping("/{roomId}")
    public List<ChatMessage> getMessagesByRoomId(@PathVariable String roomId) {
        return chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId);
    }
}
