package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.Artwork;
import com.artrithm.backendapi.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserIdOrderByAddedAtDesc(Long userId);

    boolean existsByUserIdAndArtworkId(Long userId, Long artworkId);

    void deleteByUserIdAndArtworkId(Long userId, Long artworkId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.artwork.id = :artworkId AND c.user.id <> :winnerUserId")
    void deleteAllByArtworkIdAndUserIdNot(Long artworkId, Long winnerUserId);

    List<CartItem> findByIdIn(List<Long> cartItemIds);
    void deleteAllByIdIn(List<Long> cartItemIds);
}
