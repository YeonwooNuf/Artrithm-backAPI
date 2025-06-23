package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.SubscriptionTier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionTierRepository extends JpaRepository<SubscriptionTier, Long> {

    Optional<SubscriptionTier> findByName(String name);
}
