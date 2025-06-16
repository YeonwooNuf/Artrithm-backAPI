package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    Optional<ChatRoom> findByExhibitionIdAndViewerId(Long exhibitionId, Long viewerId);
    List<ChatRoom> findByArtistId(Long artistId);

    // 작가 또는 관람자 기준으로 모든 채팅방 조회 (ChatList 용)
    List<ChatRoom> findByArtistIdOrViewerId(Long artistId, Long viewerId);
}
