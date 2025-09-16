package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    Optional<ChatMessage> findTopByRoomIdOrderBySentAtDesc(String roomId);
    void deleteByRoomId(String roomId);
    List<ChatMessage> findByRoomIdOrderBySentAtAsc(String roomId);
}
