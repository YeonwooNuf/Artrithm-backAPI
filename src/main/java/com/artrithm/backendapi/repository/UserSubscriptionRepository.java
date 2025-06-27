package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.User;
import com.artrithm.backendapi.model.UserSubscription;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UserSubscriptionRepository extends JpaRepository<UserSubscription, Long> {

    Optional<UserSubscription> findByUserAndIsActiveTrue(User user);

    List<UserSubscription> findAllByUser(User user);

    long countByIsActive(boolean isActive); // ✅ 현재 구독 중인 유저 수 조회

    @Modifying
    @Transactional
    @Query("UPDATE UserSubscription us SET us.isActive = false WHERE us.user.id = :userId AND us.isActive = true")
    void deactivateAll(@Param("userId") Long userId);
}