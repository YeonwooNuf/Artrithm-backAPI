package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.model.ChatRoom;
import com.artrithm.backendapi.dto.ChatRoomDto;
import com.artrithm.backendapi.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chatroom")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 1. 관람자 → 채팅방 생성 or 재사용
    @PostMapping("/create")
    public ChatRoom createRoom(@RequestParam Long exhibitionId,
                               @RequestParam Long artistId,
                               @RequestParam Long viewerId) {
        return chatRoomService.createOrGetRoom(exhibitionId, artistId, viewerId);
    }

    // 2. 작가 채팅방 목록 조회
    @GetMapping("/artist/{artistId}")
    public List<ChatRoom> getRoomsForArtist(@PathVariable Long artistId) {
        return chatRoomService.getRoomsByArtistId(artistId);
    }

    // 3. 채팅방 삭제 (작가가 채팅 종료 클릭 시)
    @DeleteMapping("/{roomId}")
    public void deleteRoom(@PathVariable String roomId) {
        chatRoomService.deleteRoomAndMessages(roomId);
    }

    // 4. 로그인한 사용자의 모든 채팅방 목록 조회 (작가/관람자 공통)
    @GetMapping("/user/{userId}")
    public List<ChatRoomDto> getRoomsForUser(@PathVariable Long userId) {
        return chatRoomService.getRoomsByUserId(userId);
    }

    // 5. 채팅방 정보 조회
    @GetMapping("/{roomId}/info")
    public ChatRoomDto getRoomInfo(@PathVariable String roomId, @RequestParam Long userId) {
        return chatRoomService.getRoomInfo(roomId, userId);
    }
}
