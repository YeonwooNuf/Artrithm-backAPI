package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.ArtworkDto;
import com.artrithm.backendapi.dto.UserDto;
import com.artrithm.backendapi.model.User;
import com.artrithm.backendapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ 회원가입
    public Long registerUser(UserDto dto) {
        if (userRepository.existsByLoginId(dto.getLoginId())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }
        if (userRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new IllegalArgumentException("이미 사용 중인 전화번호입니다.");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        User user = User.builder()
                .loginId(dto.getLoginId())
                .password(encodedPassword)
                .nickname(dto.getNickname())
                .birth(dto.getBirth())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .role(User.Role.USER)
                .build();

        return userRepository.save(user).getId();
    }

    // ✅ 로그인
    public UserDto login(UserDto dto) {
        User user = userRepository.findByLoginId(dto.getLoginId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        System.out.println("dfdfdfdf");

        return UserDto.builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .birth(user.getBirth())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .isArtistApproved(user.isArtistApproved())
                .artistBio(user.getArtistBio())
                .profileImage(user.getProfileImage())
                .build();
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }

    // ✅ 사용자 정보 조회
    public UserDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        return UserDto.builder()
                .id(user.getId())
                .loginId(user.getLoginId())
                .nickname(user.getNickname())
                .birth(user.getBirth())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .isArtistApproved(user.isArtistApproved())
                .artistBio(user.getArtistBio())
                .profileImage(user.getProfileImage())
                .build();
    }

    // ✅ 사용자 정보 수정 후 반환
    public UserDto updateUserInfo(Long userId, UserDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setNickname(dto.getNickname());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setBirth(dto.getBirth());
        user.setEmail(dto.getEmail());

        if ((user.isArtistApproved() || user.getRole().equals(User.Role.ADMIN))
                && dto.getArtistBio() != null) {
            user.setArtistBio(dto.getArtistBio());
        }

        MultipartFile profileFile = dto.getProfileImageFile();
        if (profileFile != null && !profileFile.isEmpty()) {
            try {
                String folder = "uploads/profile-images";
                String filename = System.currentTimeMillis() + "_" + profileFile.getOriginalFilename();
                Path path = Paths.get(folder, filename);
                Files.createDirectories(path.getParent());
                profileFile.transferTo(path);
                user.setProfileImage("/" + path.toString().replace("\\", "/"));
            } catch (IOException e) {
                throw new RuntimeException("프로필 이미지 저장 실패", e);
            }
        }

        User savedUser = userRepository.save(user);

        return UserDto.builder()
                .id(savedUser.getId())
                .loginId(savedUser.getLoginId())
                .nickname(savedUser.getNickname())
                .birth(savedUser.getBirth())
                .email(savedUser.getEmail())
                .phoneNumber(savedUser.getPhoneNumber())
                .role(savedUser.getRole().name())
                .isArtistApproved(savedUser.isArtistApproved())
                .artistBio(savedUser.getArtistBio())
                .profileImage(savedUser.getProfileImage())
                .build();
    }

    // 작가 유저만 가져오기
    public List<UserDto> getAllApprovedUserArtists(){
        List<User> approvedUsers = userRepository.findByIsArtistApprovedTrue();
        return approvedUsers.stream().map(user -> UserDto.builder()
                        .id(user.getId())
                        .nickname(user.getNickname())
                        .artistBio(user.getArtistBio())
                        .profileImage(user.getProfileImage())
                        .artworks(user.getArtworks().stream()
                                .map(ArtworkDto::fromEntity)
                                .toList())
                        .build())
                .toList();
    }
}
