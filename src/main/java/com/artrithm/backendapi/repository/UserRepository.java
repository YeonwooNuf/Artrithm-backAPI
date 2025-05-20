package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // ✅ 아이디 중복 확인
    boolean existsByLoginId(String loginId);

    // ✅ 이메일 중복 확인
    boolean existsByEmail(String email);

    // ✅ 전화번호 중복 확인
    boolean existsByPhoneNumber(String phoneNumber);

    // ✅ 로그인 시 아이디로 조회
    Optional<User> findByLoginId(String loginId);
}
