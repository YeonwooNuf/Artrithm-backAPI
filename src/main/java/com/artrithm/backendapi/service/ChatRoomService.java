package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.ChatRoomDto;
import com.artrithm.backendapi.model.ChatMessage;
import com.artrithm.backendapi.model.ChatRoom;
import com.artrithm.backendapi.model.User;
import com.artrithm.backendapi.model.Exhibition;
import com.artrithm.backendapi.repository.ChatMessageRepository;
import com.artrithm.backendapi.repository.ChatRoomRepository;
import com.artrithm.backendapi.repository.UserRepository;
import com.artrithm.backendapi.repository.ExhibitionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ExhibitionRepository exhibitionRepository;

    public ChatRoom createOrGetRoom(Long exhibitionId, Long artistId, Long viewerId) {
        Optional<ChatRoom> existingRoom = chatRoomRepository.findByExhibitionIdAndViewerId(exhibitionId, viewerId);
        if (existingRoom.isPresent()) return existingRoom.get();

        // 👇 artistId가 null이면 해당 전시의 등록자 userId를 사용
        if (artistId == null) {
            artistId = getExhibitionOwnerId(exhibitionId);
        }

        ChatRoom newRoom = ChatRoom.builder()
                .exhibitionId(exhibitionId)
                .artistId(artistId)
                .viewerId(viewerId)
                .createdAt(LocalDateTime.now())
                .build();

        return chatRoomRepository.save(newRoom);
    }

    private Long getExhibitionOwnerId(Long exhibitionId) {
        Exhibition exhibition = exhibitionRepository.findById(exhibitionId)
                .orElseThrow(() -> new RuntimeException("전시 정보 없음"));
        return exhibition.getAuthor().getId(); // 👈 전시 등록자
    }

    public List<ChatRoom> getRoomsByArtistId(Long artistId) {
        return chatRoomRepository.findByArtistId(artistId);
    }

    public void deleteRoomAndMessages(String roomId) {
        chatMessageRepository.deleteByRoomId(roomId);
        chatRoomRepository.deleteById(roomId);
    }

    public List<ChatRoomDto> getRoomsByUserId(Long userId) {
        List<ChatRoom> rooms = chatRoomRepository.findByArtistIdOrViewerId(userId, userId);

        return rooms.stream().map(room -> {
            boolean isArtist = room.getArtistId().equals(userId);
            Long otherId = isArtist ? room.getViewerId() : room.getArtistId();

            Optional<User> otherUser = userRepository.findById(otherId);
            String nickname = otherUser.map(User::getNickname).orElse("알 수 없음");
            String profileImage = otherUser.map(User::getProfileImage).orElse(null);

            ChatMessage lastMessage = chatMessageRepository
                    .findTopByRoomIdOrderBySentAtDesc(room.getId())
                    .orElse(null);

            return ChatRoomDto.builder()
                    .roomId(room.getId())
                    .otherNickname(nickname)
                    .otherProfileImage(profileImage)
                    .lastMessage(lastMessage != null ? lastMessage.getMessage() : null)
                    .lastMessageTime(lastMessage != null ? lastMessage.getSentAt() : null)
                    .build();
        }).collect(Collectors.toList());
    }

    public ChatRoomDto getRoomInfo(String roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방 없음"));

        Long otherId = room.getArtistId().equals(userId) ? room.getViewerId() : room.getArtistId();
        User otherUser = userRepository.findById(otherId)
                .orElseThrow(() -> new RuntimeException("상대방 없음"));

        return ChatRoomDto.builder()
                .roomId(room.getId())
                .otherNickname(otherUser.getNickname())
                .otherProfileImage(otherUser.getProfileImage())
                .build();
    }

    public List<ChatMessage> getMessagesByRoomId(String roomId) {
        return chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId);
    }
}
