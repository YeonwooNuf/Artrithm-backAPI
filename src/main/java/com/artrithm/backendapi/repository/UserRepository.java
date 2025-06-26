package com.artrithm.backendapi.repository;

import com.artrithm.backendapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 아이디 중복 확인
    boolean existsByLoginId(String loginId);

    // 이메일 중복 확인
    boolean existsByEmail(String email);

    // 전화번호 중복 확인
    boolean existsByPhoneNumber(String phoneNumber);

    // 로그인 시 아이디로 조회
    Optional<User> findByLoginId(String loginId);

    // 관리자 id 값 role 기반으로 조회
    Optional<User> findFirstByRole(User.Role role);
}
